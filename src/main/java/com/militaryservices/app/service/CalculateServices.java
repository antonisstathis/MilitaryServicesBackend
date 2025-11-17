package com.militaryservices.app.service;

import com.militaryservices.app.dao.*;
import com.militaryservices.app.enums.Active;
import com.militaryservices.app.dto.HistoricalData;
import com.militaryservices.app.enums.Situation;
import com.militaryservices.app.dto.SoldierProportion;
import com.militaryservices.app.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.*;

@Component
public class CalculateServices {

    @Autowired
    AssignServicesForTheFirstDay assignServicesForTheFirstDay;
    @Autowired
    EnsureUniformServiceExecution ensureUniformServiceExecution;
    @Autowired
    private SoldierAccessImpl soldierAccess;
    @Autowired
    private SoldierRepository soldierRepository;
    @Autowired
    SerOfUnitAccessImpl serOfUnitAccess;
    @Autowired
    SerOfUnitRepository serOfUnitRepository;
    @Autowired
    UserRepository userRepository;
    @Autowired
    CountServicesForEachSold countServicesForEachSold;
    @Autowired
    CalculateServicesHelper calculateServicesHelper;
    private static final Logger logger = LoggerFactory.getLogger(CalculateServices.class);

    public CalculateServices() {
    }

    public List<Soldier> calculateServices(String username,boolean isPersonnel,String group) {
        // Data structures for Soldiers
        List<Soldier> allSoldiers = loadSoldiersAndServices(username,isPersonnel,group);
        if(allSoldiers.size() == 0)
            return assignServicesForTheFirstDay.calculateServicesForTheFirstDay(username,isPersonnel,group);
        Set<Soldier> armedSoldiers = new HashSet<>();
        Set<Soldier> unarmedSoldiers = new HashSet<>();
        Map<Integer,Soldier> soldierMap = new HashMap<>();
        // The date of the next day calculation
        LocalDate nextDate = findNextCalculationDate(allSoldiers.get(0).getService().getDate());
        // Data structures for services of the unit
        Unit unit = allSoldiers.get(0).getUnit();
        List<ServiceOfUnit> servicesOfUnit = serOfUnitRepository.findByUnitAndIsPersonnelAndGroup(unit,isPersonnel,group);
        List<Service> armedServices = new ArrayList<>();
        List<Service> unarmedServices = new ArrayList<>();
        List<SoldierProportion> proportionList;
        boolean flag = true;
        // 1. Calculate the free of duty soldiers
        calculateServicesHelper.setAsAvailableAllSoldiers(allSoldiers);
        calculateServicesHelper.addServicesAndSoldiers(allSoldiers,armedSoldiers,unarmedSoldiers,soldierMap,servicesOfUnit,armedServices,unarmedServices);
        calculateServicesHelper.excludeUnavailablePersonnel(allSoldiers,armedSoldiers,unarmedSoldiers);
        int numberOfFreePersonnel = calculateNumberOfFreePersonnel(allSoldiers,isPersonnel,group);
        if((armedSoldiers.size() - armedServices.size()) >= numberOfFreePersonnel) {
            proportionList = countServicesForEachSold.getProportions(armedSoldiers,unarmedSoldiers,allSoldiers,soldierMap,true,"",isPersonnel, group);
            computeFreeSoldiers(allSoldiers, armedSoldiers, unarmedSoldiers, soldierMap, proportionList,isPersonnel, group);
            flag = false;
        }
        if(flag && ((armedSoldiers.size() - armedServices.size()) < numberOfFreePersonnel)) {
            int numOfArmedSoldForOut = armedSoldiers.size() - armedServices.size();
            proportionList = countServicesForEachSold.getProportions(armedSoldiers,unarmedSoldiers,allSoldiers,soldierMap,false, Situation.ARMED.name().toLowerCase(),isPersonnel, group);
            computeFreeSoldiersInRareCase(armedSoldiers,soldierMap,proportionList,numOfArmedSoldForOut);
            proportionList = countServicesForEachSold.getProportions(armedSoldiers,unarmedSoldiers,allSoldiers,soldierMap,false,Situation.UNARMED.name().toLowerCase(),isPersonnel, group);
            computeFreeSoldiersInRareCase(unarmedSoldiers,soldierMap,proportionList,numberOfFreePersonnel - numOfArmedSoldForOut);
        }
        // 2. Calculate services for unarmed soldiers
        calculateServicesForUnarmedSoldiers(unarmedSoldiers,unarmedServices);
        // 3. Calculate services for armed soldiers
        if(unarmedServices.size() != 0)
            setUnarmedServicesToArmedSoldiers(allSoldiers,armedSoldiers,soldierMap,unarmedServices,isPersonnel, group);
        calculateServicesForArmedSoldiers(armedSoldiers,armedServices);
        //4. Ensure that all services are distributed uniformly
        //long start = System.nanoTime();  // start timer
        //ensureUniformServiceExecution.ensureAllServicesAreUniform(allSoldiers, unit, isPersonnel, group);
        //long end = System.nanoTime();    // end timer
        //long elapsedMs = (end - start) / 1_000_000; // convert to ms
        // 5. Set dates and units
        calculateServicesHelper.setCalculationDateAndUnit(nextDate,allSoldiers);

        return allSoldiers;
    }

    private List<Soldier> loadSoldiersAndServices(String username,boolean isPersonnel, String group) {
        Optional<User> user = userRepository.findById(username);
        Unit unit = user.get().getSoldier().getUnit();
        LocalDate dateOfLastCalculation = soldierAccess.getDateOfLastCalculation(unit,isPersonnel);
        return soldierAccess.loadSoldByGroup(unit,dateOfLastCalculation,isPersonnel,group);
    }

    private LocalDate findNextCalculationDate(LocalDate lastDate) {
        return lastDate.plusDays(1);
    }

    private void computeFreeSoldiers(List<Soldier> allSoldiers, Set<Soldier> armedSoldiers,Set<Soldier> unarmedSoldiers,Map<Integer,Soldier> soldierMap,List<SoldierProportion> proportionList,boolean isPersonnel, String group) {

        int numberOfFreePersonnel = calculateNumberOfFreePersonnel(allSoldiers,isPersonnel, group);
        setFreeBasedOnProp(armedSoldiers,unarmedSoldiers,soldierMap,proportionList,numberOfFreePersonnel);
    }

    // Assign as free of duty the soldiers with the worst proportion until now
    private void setFreeBasedOnProp(Set<Soldier> armedSoldiers,Set<Soldier> unarmedSoldiers,Map<Integer,Soldier> soldierMap,List<SoldierProportion> proportionList,int numberOfFreePersonnel) {
        if(numberOfFreePersonnel == 0)
            return;

        Collections.sort(proportionList, Comparator.comparingDouble(SoldierProportion::getProportion).reversed());
        Soldier soldier;
        for(SoldierProportion soldierProportion : proportionList) {
            if(numberOfFreePersonnel == 0)
                return;
            if(numberOfFreePersonnel > 0) {
                soldier = soldierMap.get(soldierProportion.getSoldId());
                soldier.setService(new Service("out", Active.getFreeOfDuty(), LocalDate.now(), soldier.getUnit(), Active.getFreeOfDuty(),"06:00-06:00", soldier.isPersonnel()));
                removeSoldier(armedSoldiers, unarmedSoldiers, soldier);
                numberOfFreePersonnel -= 1;
            }
        }
    }

    private void computeFreeSoldiersInRareCase(Set<Soldier> soldiers,Map<Integer,Soldier> soldierMap,List<SoldierProportion> proportionList,int numberOfFreePersonnel) {
        if(numberOfFreePersonnel == 0)
            return;

        Collections.sort(proportionList, Comparator.comparingDouble(SoldierProportion::getProportion).reversed());
        Soldier soldier;
        for(SoldierProportion soldierProportion : proportionList) {
            if(numberOfFreePersonnel == 0)
                return;
            if(numberOfFreePersonnel>0) {
                soldier = soldierMap.get(soldierProportion.getSoldId());
                soldier.setService(new Service("out", Active.getFreeOfDuty(), LocalDate.now(), soldier.getUnit(), Active.getFreeOfDuty(),"06:00-06:00", soldier.isPersonnel()));
                soldiers.remove(soldier);
                numberOfFreePersonnel -= 1;
            }
        }
    }

    private void removeSoldier(Set<Soldier> armedSoldiers,Set<Soldier> unarmedSoldiers,Soldier soldier) {
        if(soldier.isArmed())
            armedSoldiers.remove(soldier);
        else
            unarmedSoldiers.remove(soldier);
    }

    private int calculateNumberOfFreePersonnel(List<Soldier> allSoldiers,boolean isPersonnel, String group) {

        List<Long> countServices = serOfUnitAccess.countServicesOfUnit(allSoldiers.get(0).getUnit(),isPersonnel,group);
        int totalNumberOfServices = countServices != null ? countServices.get(0).intValue() : 0;
        return totalSolForCalc(allSoldiers) - totalNumberOfServices;
    }

    private int totalSolForCalc(List<Soldier> allSoldiers) {
        int counter = 0;
        for(Soldier soldier : allSoldiers) {
            if(!soldier.getActive().equals(Active.getFreeOfDuty()))
                counter += 1;
        }

        return counter;
    }

    private void calculateServicesForUnarmedSoldiers(Set<Soldier> unarmedSoldiers,List<Service> unarmedServices) {
        Random random = new Random();
        int randomIndex;
        Service service;
        for(Soldier sold : unarmedSoldiers){
            randomIndex = random.nextInt(unarmedServices.size());
            service = unarmedServices.get(randomIndex);
            sold.setService(service);
            unarmedServices.remove(randomIndex);
            if(unarmedServices.size()==0)
                break;
        }
    }

    private void setUnarmedServicesToArmedSoldiers(List<Soldier> allSoldiers,Set<Soldier> armedSoldiers,Map<Integer,Soldier> soldierMap,List<Service> unarmedServices,boolean isPersonnel, String group) {
        List<HistoricalData> historicalData = soldierAccess.getHistoricalDataDesc(allSoldiers.get(0).getUnit(),Situation.ARMED.name().toLowerCase(),isPersonnel, group, Active.ACTIVE.name().toLowerCase());

        Map<Integer,Soldier> soldiersMap = new HashMap<>();
        for(Soldier soldier : allSoldiers)
            soldiersMap.put(soldier.getId(),soldier);

        if(historicalData.size()<armedSoldiers.size())
            countServicesForEachSold.addTheRestArmedOnes(historicalData,soldierMap,armedSoldiers);
        Soldier soldier;
        int soldId;
        for(HistoricalData hd : historicalData) {
            soldId = hd.getSoldierId();
            soldier = soldiersMap.get(soldId);
            if("out".equals(soldier.getService().getServiceName()))
                continue;
            soldier.setService(unarmedServices.get(0));
            unarmedServices.remove(0);
            armedSoldiers.remove(soldier);
            if(unarmedServices.size() == 0)
                break;
        }
    }

    private void calculateServicesForArmedSoldiers(Set<Soldier> armedSoldiers,List<Service> armedServices) {
        Random random = new Random();
        int randomIndex;
        Service service;
        for(Soldier sold : armedSoldiers){
            randomIndex = random.nextInt(armedServices.size());
            service = armedServices.get(randomIndex);
            sold.setService(service);
            armedServices.remove(randomIndex);
        }
    }

    public void saveNewServices(List<Soldier> allSoldiers) {
        try {
            soldierAccess.saveSoldiers(allSoldiers);
        } catch (IOException | SQLException e) {
            logger.error("Failed to save soldiers: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

}
package com.militaryservices.app.service;

import com.militaryservices.app.dao.*;
import com.militaryservices.app.dto.ServiceRatioDto;
import com.militaryservices.app.enums.Active;
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
public class EnsureUniformServiceExecution {

    @Autowired
    AssignServicesForTheFirstDay assignServicesForTheFirstDay;
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

    public EnsureUniformServiceExecution() {
    }

    public List<Soldier> ensureAllServicesAreUniform(List<Soldier> allSoldiers) {


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

    private List<Service> calculateServicesForUnarmedSoldiers(Map<Integer, Soldier> allSoldiers ,List<Service> unarmedServices,
                                                              Unit unit, boolean isPersonnel, String group) {

        // Add all available armed soldiers to a new HashMap to access them in O(1) time complexity using the soldier id
        Soldier soldier;
        Map<Integer, Soldier> soldiersIds = new HashMap<>();
        for (Map.Entry<Integer, Soldier> entry : allSoldiers.entrySet()) {
            soldier = entry.getValue();
            if(!soldier.isArmed() && soldier.getService().getServiceName().equals("available"))
                soldiersIds.put(soldier.getId(), soldier);
        }
        if(soldiersIds.size() == 0)
            return unarmedServices;

        List<ServiceRatioDto> ratios;
        Map<Service,Service> mapOfServices = new HashMap<>();
        List<Service> unarmedServicesForArmedSoldiers = new ArrayList<>();
        for(Service service : unarmedServices)
            mapOfServices.put(service, service);

        for(Service service : unarmedServices) {
            ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, service.getServiceName(),
                    Situation.UNARMED.name().toLowerCase(), isPersonnel, group, Active.ACTIVE.name().toLowerCase(),soldiersIds);
            soldier = allSoldiers.get(ratios.get(0).getSoldId());
            soldier.setService(service);
            soldiersIds.remove(soldier.getId());
            mapOfServices.remove(service);
            if(soldiersIds.size() == 0)
                break;
        }

        for (Map.Entry<Service, Service> entry : mapOfServices.entrySet())
            unarmedServicesForArmedSoldiers.add(entry.getKey());

        return unarmedServicesForArmedSoldiers;
    }

    private void setUnarmedServicesToArmedSoldiers(Unit unit,Set<Soldier> armedSoldiers,Map<Integer,Soldier> soldierMap,List<Service> unarmedServices,boolean isPersonnel, String group) {

        Soldier soldier;
        Map<Integer, Soldier> soldiersIds = new HashMap<>();
        for (Map.Entry<Integer, Soldier> entry : soldierMap.entrySet()) {
            soldier = entry.getValue();
            if(soldier.isArmed() && soldier.getService().getServiceName().equals("available"))
                soldiersIds.put(soldier.getId(), soldier);
        }

        List<ServiceRatioDto> ratios;
        for(Service service : unarmedServices) {
            ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, service.getServiceName(), Situation.UNARMED.name().toLowerCase(),
                    isPersonnel, group, Active.ACTIVE.name().toLowerCase(),soldiersIds);
            soldier = soldierMap.get(ratios.get(0).getSoldId());
            soldier.setService(service);
            armedSoldiers.remove(soldier);
            soldiersIds.remove(soldier.getId());
        }
    }

    private void calculateServicesForArmedSoldiers(Map<Integer, Soldier> allSoldiers ,List<Service> armedServices,
                                                   Unit unit, boolean isPersonnel, String group) {

        // Add all available armed soldiers to a new HashMap to access them in O(1) time complexity using the soldier id (average case)
        Soldier soldier;
        Map<Integer, Soldier> soldiersIds = new HashMap<>();
        for (Map.Entry<Integer, Soldier> entry : allSoldiers.entrySet()) {
            soldier = entry.getValue();
            if(soldier.isArmed() && soldier.getService().getServiceName().equals("available"))
                soldiersIds.put(soldier.getId(), soldier);
        }

        List<ServiceRatioDto> ratios;
        for(Service service : armedServices) {
            ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, service.getServiceName(), Situation.ARMED.name().toLowerCase(), isPersonnel,
                    group, Active.ACTIVE.name().toLowerCase(),soldiersIds);
            soldier = allSoldiers.get(ratios.get(0).getSoldId());
            soldier.setService(service);
            soldiersIds.remove(soldier.getId());
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
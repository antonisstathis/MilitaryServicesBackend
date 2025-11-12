package com.militaryservices.app.service;

import com.militaryservices.app.dao.*;
import com.militaryservices.app.entity.*;
import com.militaryservices.app.enums.Active;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class AssignServicesForTheFirstDay {

    @Autowired
    private CalculateServicesHelper calculateServicesHelper;
    @Autowired
    private SoldierRepository soldierRepository;
    @Autowired
    SerOfUnitRepository serOfUnitRepository;
    @Autowired
    UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(CalculateServices.class);

    public AssignServicesForTheFirstDay() {
    }

    protected List<Soldier> calculateServicesForTheFirstDay(String username, boolean isPersonnel, String group) {
        Optional<User> user = userRepository.findById(username);
        Unit unit = user.get().getSoldier().getUnit();
        List<Soldier> allSoldiers = soldierRepository.findByUnitAndDischargedAndIsPersonnelAndGroup(unit, false, isPersonnel, group);
        Set<Soldier> armedSoldiers = new HashSet<>();
        Set<Soldier> unarmedSoldiers = new HashSet<>();
        Map<Integer,Soldier> soldierMap = new HashMap<>();
        List<ServiceOfUnit> servicesOfUnit = serOfUnitRepository.findByUnitAndIsPersonnelAndGroup(unit,isPersonnel,group);
        List<Service> armedServices = new ArrayList<>();
        List<Service> unarmedServices = new ArrayList<>();
        LocalDate nextDate = LocalDate.now();
        calculateServicesHelper.addServicesAndSoldiers(allSoldiers,armedSoldiers,unarmedSoldiers,soldierMap,servicesOfUnit,armedServices,unarmedServices);
        calculateServicesHelper.excludeUnavailablePersonnel(allSoldiers,armedSoldiers,unarmedSoldiers);

        // 1. Assign unarmed services to all unarmed soldiers
        int unarmedService = assignUnarmedServices(allSoldiers, unarmedSoldiers, unarmedServices);

        // 2. Assign the remaining unarmed services to armed soldiers
        assignRemainingUnamredServices(allSoldiers, armedSoldiers, unarmedServices, unarmedService);

        // 3. Assign all the armed services to armed soldiers
        assignArmedServices(allSoldiers,armedSoldiers,armedServices);

        //4. Set free of duty the remaining personnel
        setFreeOfDutyTheRemainingPersonnel(armedSoldiers,unarmedSoldiers);

        calculateServicesHelper.setCalculationDateAndUnit(nextDate,allSoldiers);

        return allSoldiers;
    }

    private int assignUnarmedServices(List<Soldier> allSoldiers, Set<Soldier> unarmedSoldiers, List<Service> unarmedServices) {
        int unarmedService = 0;
        for(Soldier soldier : allSoldiers) {
            if(unarmedService < unarmedServices.size() && !soldier.isArmed() && !soldier.getActive().equals(Active.getFreeOfDuty())) {
                soldier.setService(unarmedServices.get(unarmedService));
                unarmedService += 1;
                unarmedSoldiers.remove(soldier);
            }
        }

        return unarmedService;
    }

    private void assignRemainingUnamredServices(List<Soldier> allSoldiers, Set<Soldier> armedSoldiers, List<Service> unarmedServices, int unarmedService) {
        for(Soldier soldier : allSoldiers) {
            if(unarmedService == unarmedServices.size())
                break;
            if(unarmedService < unarmedServices.size() && soldier.isArmed()) {
                soldier.setService(unarmedServices.get(unarmedService));
                unarmedService += 1;
                armedSoldiers.remove(soldier);
            }
        }
    }

    private void assignArmedServices(List<Soldier> allSoldiers, Set<Soldier> armedSoldiers, List<Service> armedServices) {
        int armedService = 0;
        for(Soldier soldier : allSoldiers) {
            if(armedService < armedServices.size() && armedSoldiers.contains(soldier)) {
                soldier.setService(armedServices.get(armedService));
                armedService += 1;
                armedSoldiers.remove(soldier);
            }
        }
    }

    private void setFreeOfDutyTheRemainingPersonnel(Set<Soldier> armedSoldiers, Set<Soldier> unarmedSoldiers) {
        for(Soldier soldier : armedSoldiers)
            soldier.setService(new Service("out", Active.getFreeOfDuty(), LocalDate.now(), soldier.getUnit(), Active.getFreeOfDuty(),"06:00-06:00", soldier.isPersonnel()));

        for(Soldier soldier : unarmedSoldiers)
            soldier.setService(new Service("out", Active.getFreeOfDuty(), LocalDate.now(), soldier.getUnit(), Active.getFreeOfDuty(),"06:00-06:00", soldier.isPersonnel()));

    }
}

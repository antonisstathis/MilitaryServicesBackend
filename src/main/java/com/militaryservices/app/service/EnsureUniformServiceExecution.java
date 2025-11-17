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

}
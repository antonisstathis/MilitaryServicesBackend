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

    public List<Soldier> ensureAllServicesAreUniform(List<Soldier> allSoldiers, Unit unit, boolean isPersonnel, String group ) {

        Map<Integer, Soldier> soldierMap = new HashMap<>();
        Set<Soldier> armedSoldiers = new HashSet<>();
        Set<Soldier> unarmedSoldiers = new HashSet<>();
        List<ServiceOfUnit> servicesOfUnit = serOfUnitRepository.findByUnitAndIsPersonnelAndGroup(unit,isPersonnel,group);
        List<Service> armedServices = new ArrayList<>();
        List<Service> unarmedServices = new ArrayList<>();
        calculateServicesHelper.addServicesAndSoldiers(allSoldiers,armedSoldiers,unarmedSoldiers,soldierMap,servicesOfUnit,armedServices,unarmedServices);

        unarmedServices = calculateServicesForUnarmedSoldiers(soldierMap, unarmedServices , unit, isPersonnel, group);
        if(unarmedServices.size() != 0)
            setUnarmedServicesToArmedSoldiers(unit, soldierMap, unarmedServices, isPersonnel, group);
        calculateServicesForArmedSoldiers(soldierMap, armedServices, unit, isPersonnel, group);

        return allSoldiers;
    }

    private List<Service> calculateServicesForUnarmedSoldiers(Map<Integer, Soldier> allSoldiers, List<Service> unarmedServices, Unit unit, boolean isPersonnel, String group) {

        // Add all available armed soldiers to a new HashMap to access them in O(1) time complexity using the soldier id
        Soldier soldier;
        Map<Integer, Soldier> soldiersIds = new HashMap<>();
        for (Map.Entry<Integer, Soldier> entry : allSoldiers.entrySet()) {
            soldier = entry.getValue();
            if(!soldier.isArmed() && !soldier.getService().getServiceName().equals("out"))
                soldiersIds.put(soldier.getId(), soldier);
        }
        if(soldiersIds.size() == 0)
            return unarmedServices;

        List<ServiceRatioDto> ratios;
        Map<Service,Service> mapOfServices = new HashMap<>();
        List<Service> unarmedServicesForArmedSoldiers = new ArrayList<>();
        for(Service service : unarmedServices)
            mapOfServices.put(service, service);

        Random random = new Random();
        if(random.nextBoolean())
            unarmedServices = invertServices(unarmedServices);

        for(Service service : unarmedServices) {
            ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, service.getServiceName(),
                    Situation.UNARMED.name().toLowerCase(), isPersonnel, group, Active.ACTIVE.name().toLowerCase(), soldiersIds);
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

    private List<Service> invertServices(List<Service> unarmedServices) {

        List<Service> invertedServices = new ArrayList<>();
        for(int i = unarmedServices.size() - 1; i >= 0; i--) {
            invertedServices.add(unarmedServices.get(i));
        }

        return invertedServices;
    }

    private void setUnarmedServicesToArmedSoldiers(Unit unit, Map<Integer,Soldier> soldierMap, List<Service> unarmedServices, boolean isPersonnel, String group) {
        Soldier soldier;
        Map<Integer, Soldier> soldiersIds = new HashMap<>();
        for (Map.Entry<Integer, Soldier> entry : soldierMap.entrySet()) {
            soldier = entry.getValue();
            if(soldier.isArmed() && !soldier.getService().isArmed() && !soldier.getService().getServiceName().equals("out"))
                soldiersIds.put(soldier.getId(), soldier);
        }

        Random random = new Random();
        if(random.nextBoolean())
            unarmedServices = invertServices(unarmedServices);

        List<ServiceRatioDto> ratios;
        for(Service service : unarmedServices) {
            ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, service.getServiceName(), Situation.UNARMED.name().toLowerCase(),
                    isPersonnel, group, Active.ACTIVE.name().toLowerCase(),soldiersIds);
            soldier = soldierMap.get(ratios.get(0).getSoldId());
            soldier.setService(service);
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
            if(soldier.isArmed() && soldier.getService().isArmed())
                soldiersIds.put(soldier.getId(), soldier);
        }

        Random random = new Random();
        if(random.nextBoolean())
            armedServices = invertServices(armedServices);

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
package com.militaryservices.app.service;

import com.militaryservices.app.dao.*;
import com.militaryservices.app.dto.ServiceRatioDto;
import com.militaryservices.app.enums.Active;
import com.militaryservices.app.enums.Situation;
import com.militaryservices.app.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
public class EnsureUniformServiceExecution {

    @Autowired
    SerOfUnitRepository serOfUnitRepository;
    @Autowired
    CountServicesForEachSold countServicesForEachSold;
    @Autowired
    CalculateServicesHelper calculateServicesHelper;
    private static final Logger logger = LoggerFactory.getLogger(CalculateServices.class);

    public EnsureUniformServiceExecution() {
    }

    public List<Soldier> ensureAllServicesAreUniform(List<Soldier> allSoldiers, Unit unit, boolean isPersonnel, String group ) {

        logger.info("\n----------------------------------------------------------------------------------------------------------ensureAllServicesAreUniform Method----------------------------------------------------------------------------------------------------------");
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

        logger.info("\n----------------------------------------------------------------------------------------------------------calculateServicesForUnarmedSoldiers method----------------------------------------------------------------------------------------------------------");
        Map<Service, Service> mapOfServices = new HashMap<>();
        Map<Integer, Soldier> soldiersIds = new HashMap<>();
        if(!addServicesToDataStructures(allSoldiers, unarmedServices, mapOfServices, soldiersIds))
            return unarmedServices;

        List<ServiceRatioDto> ratiosForService;
        Soldier soldier;
        Map<String, List<ServiceRatioDto>> ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, Situation.UNARMED.name().toLowerCase(),
                isPersonnel, group, Active.ACTIVE.name().toLowerCase(), soldiersIds);
        for (Service service : unarmedServices) {
            ratiosForService = ratios.get(service.getServiceName());
            soldier = findSoldier(allSoldiers, ratiosForService, soldiersIds);
            soldier.setService(service);
            logger.info("SERVICE ASSIGNED - soldierId={}, soldierName={}, soldierSurname={}, situation={} unit={}, serviceName={}, date={}, personnel={}",
                    soldier.getId(), soldier.getName(), soldier.getSurname(), soldier.getSituation(), soldier.getUnit().getNameOfUnit(), soldier.getService().getServiceName(),
                    soldier.getService().getDate(), soldier.isPersonnel());
            soldiersIds.remove(soldier.getId());
            ratios.remove(service.getServiceName());
            mapOfServices.remove(service);
            if (soldiersIds.size() == 0)
                break;
        }

        List<Service> unarmedServicesForArmedSoldiers = new ArrayList<>();
        for (Map.Entry<Service, Service> entry : mapOfServices.entrySet())
            unarmedServicesForArmedSoldiers.add(entry.getKey());

        return unarmedServicesForArmedSoldiers;
    }

    private void setUnarmedServicesToArmedSoldiers(Unit unit, Map<Integer,Soldier> soldierMap, List<Service> unarmedServices, boolean isPersonnel, String group) {
        logger.info("\n----------------------------------------------------------------------------------------------------------setUnarmedServicesToArmedSoldiers method----------------------------------------------------------------------------------------------------------");
        Soldier soldier;
        Map<Integer, Soldier> soldiersIds = new HashMap<>();
        for (Map.Entry<Integer, Soldier> entry : soldierMap.entrySet()) {
            soldier = entry.getValue();
            if(soldier.isArmed() && !soldier.getService().isArmed() && !soldier.getService().getArmed().equals(Active.getFreeOfDuty()))
                soldiersIds.put(soldier.getId(), soldier);
        }

        Random random = new Random();
        if(random.nextBoolean())
            unarmedServices = invertServices(unarmedServices);

        List<ServiceRatioDto> ratiosForService;
        Map<String, List<ServiceRatioDto>> ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, Situation.UNARMED.name().toLowerCase(),
                isPersonnel, group, Active.ACTIVE.name().toLowerCase(), soldiersIds);
        for(Service service : unarmedServices) {
            ratiosForService = ratios.get(service.getServiceName());
            soldier = findSoldier(soldierMap, ratiosForService, soldiersIds);
            soldier.setService(service);
            logger.info("SERVICE ASSIGNED - soldierId={}, soldierName={}, soldierSurname={}, situation={} unit={}, serviceName={}, date={}, personnel={}",
                    soldier.getId(), soldier.getName(), soldier.getSurname(), soldier.getSituation(), soldier.getUnit().getNameOfUnit(), soldier.getService().getServiceName(),
                    soldier.getService().getDate(), soldier.isPersonnel());
            soldiersIds.remove(soldier.getId());
        }
    }

    private void calculateServicesForArmedSoldiers(Map<Integer, Soldier> allSoldiers ,List<Service> armedServices,
                                                   Unit unit, boolean isPersonnel, String group) {

        // Add all available armed soldiers to a new HashMap to access them in O(1) time complexity using the soldier id (average case)
        logger.info("\n----------------------------------------------------------------------------------------------------------calculateServicesForArmedSoldiers method----------------------------------------------------------------------------------------------------------");
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

        List<ServiceRatioDto> ratiosForService;
        Map<String, List<ServiceRatioDto>> ratios = countServicesForEachSold.getRatioOfServicesForEachSoldier(unit, Situation.ARMED.name().toLowerCase(),
                isPersonnel, group, Active.ACTIVE.name().toLowerCase(), soldiersIds);
        for(Service service : armedServices) {
            ratiosForService = ratios.get(service.getServiceName());
            soldier = findSoldier(allSoldiers, ratiosForService, soldiersIds);
            soldier.setService(service);
            logger.info("SERVICE ASSIGNED - soldierId={}, soldierName={}, soldierSurname={}, situation={} unit={}, serviceName={}, date={}, personnel={}",
                    soldier.getId(), soldier.getName(), soldier.getSurname(), soldier.getSituation(), soldier.getUnit().getNameOfUnit(), soldier.getService().getServiceName(),
                    soldier.getService().getDate(), soldier.isPersonnel());
            soldiersIds.remove(soldier.getId());
        }
    }

    private boolean addServicesToDataStructures(Map<Integer, Soldier> allSoldiers, List<Service> unarmedServices, Map<Service, Service> mapOfServices,  Map<Integer, Soldier> soldiersIds) {
        // Add all available armed soldiers to a new HashMap to access them in O(1) time complexity using the soldier id
        Soldier soldier;
        for (Map.Entry<Integer, Soldier> entry : allSoldiers.entrySet()) {
            soldier = entry.getValue();
            if (!soldier.isArmed() && !soldier.getService().getArmed().equals(Active.getFreeOfDuty()))
                soldiersIds.put(soldier.getId(), soldier);
        }
        if (soldiersIds.size() == 0)
            return false;

        for (Service service : unarmedServices)
            mapOfServices.put(service, service);

        Random random = new Random();
        if (random.nextBoolean())
            unarmedServices = invertServices(unarmedServices);

        return true;
    }

    private List<Service> invertServices(List<Service> services) {

        List<Service> invertedServices = new ArrayList<>();
        for(int i = services.size() - 1; i >= 0; i--) {
            invertedServices.add(services.get(i));
        }

        return invertedServices;
    }

    private Soldier findSoldier(Map<Integer, Soldier> allSoldiers, List<ServiceRatioDto> ratiosForService, Map<Integer, Soldier> soldiersIds) {
        Soldier soldier = new Soldier();
        int soldId;
        for(int i = 0; i < ratiosForService.size(); i++) {
            soldId = ratiosForService.get(i).getSoldId();
            if (soldiersIds.containsKey(soldId))
                soldier = allSoldiers.get(ratiosForService.get(i).getSoldId());
        }

        return soldier;
    }

}
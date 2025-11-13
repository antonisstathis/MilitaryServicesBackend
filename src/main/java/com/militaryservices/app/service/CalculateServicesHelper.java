package com.militaryservices.app.service;

import com.militaryservices.app.dao.*;
import com.militaryservices.app.entity.Service;
import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.enums.Active;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Component
public class CalculateServicesHelper {

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

    public CalculateServicesHelper() {

    }

    protected void addServicesAndSoldiers(List<Soldier> allSoldiers, Set<Soldier> armedSoldiers, Set<Soldier> unarmedSoldiers, Map<Integer,Soldier> soldierMap, List<ServiceOfUnit> servicesOfUnit, List<Service> armedServices, List<Service> unarmedServices) {
        armedServices.clear();
        unarmedServices.clear();
        Unit unit = allSoldiers.get(0).getUnit();

        for(ServiceOfUnit serviceOfUnit : servicesOfUnit) {
            if(serviceOfUnit.isArmed())
                armedServices.add(new Service(serviceOfUnit.getServiceName(),serviceOfUnit.getArmed(),LocalDate.now(),unit,serviceOfUnit.getDescription(),serviceOfUnit.getShift(), serviceOfUnit.isPersonnel()));
            else
                unarmedServices.add(new Service(serviceOfUnit.getServiceName(),serviceOfUnit.getArmed(),LocalDate.now(),unit,serviceOfUnit.getDescription(),serviceOfUnit.getShift(), serviceOfUnit.isPersonnel()));
        }

        addSoldiers(allSoldiers,armedSoldiers,unarmedSoldiers,soldierMap);
    }

    private void addSoldiers(List<Soldier> allSoldiers,Set<Soldier> armedSoldiers,Set<Soldier> unarmedSoldiers,Map<Integer,Soldier> soldierMap) {
        armedSoldiers.clear();
        unarmedSoldiers.clear();
        for(Soldier sold : allSoldiers){
            if(sold.isArmed())
                armedSoldiers.add(sold);
            else
                unarmedSoldiers.add(sold);
            soldierMap.put(sold.getId(),sold);
        }
    }

    protected void setCalculationDateAndUnit(LocalDate nextDate, List<Soldier> allSoldiers) {
        for(Soldier sold : allSoldiers) {
            sold.getService().setDate(nextDate);
            sold.getService().setUnit(sold.getUnit());
            sold.getService().setGroup(sold.getGroup());
        }
    }

    protected void excludeUnavailablePersonnel(List<Soldier> allSoldiers,Set<Soldier> armedSoldiers,Set<Soldier> unarmedSoldiers) {
        // Set the free of duty soldiers.
        Soldier sold;
        for (int i = 0; i < allSoldiers.size(); i++) {
            sold = allSoldiers.get(i);
            if (!sold.checkIfActive()) {
                sold.setService(new Service(Active.getFreeOfDuty(), Active.getFreeOfDuty(), LocalDate.now(), sold.getUnit()));
                if(sold.isArmed())
                    armedSoldiers.remove(sold);
                else
                    unarmedSoldiers.remove(sold);
            }
        }
    }

    protected void setAsAvailableAllSoldiers(List<Soldier> allSoldiers) {

        for(Soldier soldier : allSoldiers)
            soldier.setService(new Service("available","available",LocalDate.now()));
    }
}

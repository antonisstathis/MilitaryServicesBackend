package com.militaryservices.app.test;

import com.militaryservices.app.controller.SoldiersController;
import com.militaryservices.app.dao.SerOfUnitRepository;
import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dao.UserRepository;
import com.militaryservices.app.entity.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.*;

@Component
public class CheckOutput {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SoldierAccessImpl soldierAccess;
    @Autowired
    SerOfUnitRepository serOfUnitRepository;
    private static final Logger logger = LoggerFactory.getLogger(SoldiersController.class);

    public CheckOutput( ) {
    }


    public boolean checkResults(String username) {
        logger.info("CHECK METHOD CALLED for user {}", username);
        Optional<User> user = userRepository.findById(username);
        Unit unit = user.get().getSoldier().getUnit();
        LocalDate dateOfLastCalculation = soldierAccess.getDateOfLastCalculation(unit,false);
        List<Soldier> allSoldiers = soldierAccess.loadSold(unit,dateOfLastCalculation,false);
        List<ServiceOfUnit> servicesOfUnit = serOfUnitRepository.findByUnitAndIsPersonnel(user.get().getSoldier().getUnit(),false);
        Map<String, Integer> servicesMap = new HashMap<>();
        int freq;
        for (ServiceOfUnit serviceOfUnit : servicesOfUnit) {
            if (servicesMap.containsKey(serviceOfUnit.getServiceName())) {
                freq = servicesMap.get(serviceOfUnit.getServiceName());
                servicesMap.put(serviceOfUnit.getServiceName(), freq + 1);
            } else
                servicesMap.put(serviceOfUnit.getServiceName(), 1);
        }

        String serviceName;
        for (Soldier soldier : allSoldiers) {
            serviceName = soldier.getService().getServiceName();
            if (servicesMap.containsKey(serviceName)) {
                freq = servicesMap.get(serviceName);
                servicesMap.put(serviceName, freq - 1);
            }
            if (!servicesMap.containsKey(serviceName) && !"out".equals(soldier.getService().getServiceName()) && !"free of duty".equals(soldier.getService().getServiceName())) {
                logger.error("CHECK FAILED [UNEXPECTED SERVICE] - user={}, unit={}, soldierId={}, service={}", username, unit.getNameOfUnit(), soldier.getId(), serviceName);
                return false;
            }
        }

        for (Map.Entry<String, Integer> entry : servicesMap.entrySet()) {
            freq = entry.getValue();
            if (freq != 0) {
                logger.error("CHECK FAILED [UNEXPECTED SERVICE] - user={}, unit={}, service={}", username, unit.getNameOfUnit(), entry.getKey());
                return false;
            }
        }

        logger.info("CHECKS SUCCEED for user {}", username);
        return true;
    }

}

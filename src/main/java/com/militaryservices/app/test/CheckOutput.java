package com.militaryservices.app.test;

import com.militaryservices.app.dao.SerOfUnitRepository;
import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dao.UserRepository;
import com.militaryservices.app.entity.ServiceOfArmy;
import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class CheckOutput {

    @Autowired
    UserRepository userRepository;
    @Autowired
    SoldierAccessImpl soldierAccess;
    @Autowired
    SerOfUnitRepository serOfUnitRepository;

    public CheckOutput( ) {
    }


    public boolean checkResults(String username) {
        Optional<User> user = userRepository.findById(username);
        List<Soldier> allSoldiers = soldierAccess.loadSold(user.get().getSoldier().getUnit());
        List<ServiceOfUnit> servicesOfUnit = serOfUnitRepository.findByUnit(user.get().getSoldier().getUnit());
        Map<String, Integer> servicesMap = new HashMap<>();
        ServiceOfArmy serviceOfArmy;
        int freq;
        for (ServiceOfUnit serviceOfUnit : servicesOfUnit) {
            serviceOfArmy = serviceOfUnit.getServiceOfArmy();
            if (servicesMap.containsKey(serviceOfArmy.getServiceName())) {
                freq = servicesMap.get(serviceOfArmy.getServiceName());
                servicesMap.put(serviceOfArmy.getServiceName(), freq + 1);
            } else
                servicesMap.put(serviceOfArmy.getServiceName(), 1);
        }

        String serviceName;
        for (Soldier soldier : allSoldiers) {
            serviceName = soldier.getService().getServiceName();
            if (servicesMap.containsKey(serviceName)) {
                freq = servicesMap.get(serviceName);
                servicesMap.put(serviceName, freq - 1);
            }
            if (!servicesMap.containsKey(serviceName) && !"out".equals(soldier.getService().getServiceName()))
                return false;
        }

        for (Map.Entry<String, Integer> entry : servicesMap.entrySet()) {
            freq = entry.getValue();
            if (freq != 0)
                return false;
        }

        return true;
    }

}

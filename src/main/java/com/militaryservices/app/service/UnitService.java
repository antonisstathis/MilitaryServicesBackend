package com.militaryservices.app.service;

import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.security.SanitizationUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UnitService {
    @Autowired
    SoldierAccessImpl soldierAccess;
    public String findNameOfUnit(UserDto user) {
        Soldier soldier = soldierAccess.findSoldierById(user.getSoldierId());
        String nameOfUnit = soldier.getUnit().getNameOfUnit();
        return SanitizationUtil.sanitize(nameOfUnit);
    }
}

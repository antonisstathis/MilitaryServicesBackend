package com.militaryservices.app.security;

import com.militaryservices.app.dto.SoldierPersonalDataDto;
import com.militaryservices.app.dto.SoldierUnitDto;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.service.SoldierService;
import com.militaryservices.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class UserPermission {

    @Autowired
    private SoldierService soldierService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    public boolean checkIfUserHasAccess(String token, UserDto userDto,String situation,String active) {
        String username = userDto.getUsername();
        if(!jwtUtil.isTokenValid(token))
            return false;
        int soldId = Integer.valueOf(jwtUtil.extractUsername(token));
        boolean isPermitted = checkIfSoldierBelongsToUser(soldId,username);
        if(!isPermitted)
            return false;

        boolean areOptionsValid = CheckOptions.checkOptions(situation,active);
        if(!areOptionsValid)
            return false;

        return true;
    }

    public boolean checkIfSoldierBelongsToUser(int soldId,String username) {
        SoldierUnitDto soldier = soldierService.findSoldierUnit(soldId);
        UserDto userDto = userService.findUser(username);
        SoldierUnitDto user = soldierService.findSoldierUnit(userDto.getSoldierId());

        return soldier.getUnit().getId() == user.getUnit().getId() ? true : false;
    }
}

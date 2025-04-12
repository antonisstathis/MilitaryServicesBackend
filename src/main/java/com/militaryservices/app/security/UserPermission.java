package com.militaryservices.app.security;

import com.militaryservices.app.dto.SoldierUnitDto;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.service.SoldierService;
import com.militaryservices.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class UserPermission {

    @Autowired
    private SoldierService soldierService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    public boolean checkIfUserHasAccess(String token, HttpServletRequest request) {
        if(!jwtUtil.isTokenValid(token))
            return false;
        int soldId = Integer.valueOf(jwtUtil.extractUsername(token));
        boolean isPermitted = checkIfSoldierBelongsToUser(soldId,jwtUtil.extractUsername(request));
        if(!isPermitted)
            return false;

        return true;
    }

    public boolean checkIfSoldierBelongsToUser(int soldId,String username) {
        SoldierUnitDto soldierToUpdate = soldierService.findSoldier(soldId);
        Optional<User> optionalUser = userService.findUser(username);
        SoldierUnitDto user = soldierService.findSoldier(optionalUser.get().getSoldier().getId());

        return soldierToUpdate.getUnit().getId() == user.getUnit().getId() ? true : false;
    }
}

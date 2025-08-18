package com.militaryservices.app.controller;

import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class UserRequestHelper {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    public UserRequestHelper(UserService userService, JwtUtil jwtUtil) {
        this.userService = userService;
        this.jwtUtil = jwtUtil;
    }

    public UserDto getUserFromRequest(HttpServletRequest request) {
        String username = jwtUtil.extractUsername(request);
        return userService.findUser(username);
    }
}


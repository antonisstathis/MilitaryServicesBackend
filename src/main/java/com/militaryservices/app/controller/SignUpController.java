package com.militaryservices.app.controller;

import com.militaryservices.app.dto.SignupRequest;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SignUpController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    UserService userService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/api/signUp")
    public ResponseEntity<?> performSignup(@RequestHeader("X-Client-DN") String userData,
                                           @RequestHeader("X-Client-Verify") String verify, @RequestBody SignupRequest signupRequest) {

        return userService.insertNewUser(userData,verify,signupRequest);
    }

}

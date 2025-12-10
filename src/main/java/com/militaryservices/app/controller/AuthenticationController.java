package com.militaryservices.app.controller;

import com.militaryservices.app.dto.LoginRequest;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.service.AuthorityService;
import com.militaryservices.app.service.UserService;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import org.slf4j.Logger;

@RestController
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    UserService userService;
    @Autowired
    AuthorityService authorityService;
    private static final Logger logger = LoggerFactory.getLogger(AuthenticationController.class);

    @PostMapping("/api/performLogin")
    public ResponseEntity<?> performLogin(@RequestBody LoginRequest loginRequest) {
        try {
            UserDto user = userService.findUser(loginRequest.getUsername());
            if(user == null) {
                logger.warn("Login attempt failed: username '{}' not found", loginRequest.getUsername());
                return ResponseEntity.status(401).body("Invalid username");
            }

            if (!encoder.matches(loginRequest.getPassword(), user.getPassword())) {
                logger.warn("Login attempt failed: invalid password for username '{}'", loginRequest.getUsername());
                return ResponseEntity.status(401).body("Invalid password");
            }

            List<String> authorities = authorityService.findRolesByUsername(user);
            String token = jwtUtil.generateToken(loginRequest.getUsername(),authorities);

            logger.info("User '{}' successfully logged in", loginRequest.getUsername());
            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
            logger.error("BadCredentialsException during login for user '{}'", loginRequest.getUsername(), e);
            return ResponseEntity.status(401).body("Invalid username or password");
        }
    }

    class JwtResponse {
        private String token;

        public JwtResponse(String token) {
            this.token = token;
        }

        public String getToken() {
            return token;
        }

        public void setToken(String token) {
            this.token = token;
        }
    }
}




package com.militaryservices.app.controller;

import com.militaryservices.app.dto.LoginRequest;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
//@RequestMapping("/")
public class AuthenticationController {

    @Autowired
    private JwtUtil jwtUtil;
    @Autowired
    private PasswordEncoder encoder;
    @Autowired
    UserService userService;

    @PostMapping("/performLogin")
    public ResponseEntity<?> performLogin(@RequestBody LoginRequest loginRequest) {
        try {
            Optional<User> optionalUser = userService.findUser(loginRequest.getUsername());
            if(optionalUser.isEmpty())
                return ResponseEntity.status(401).body("Invalid username");

            if (!encoder.matches(loginRequest.getPassword(), optionalUser.get().getPassword()))
                return ResponseEntity.status(401).body("Invalid password");

            String token = jwtUtil.generateToken(loginRequest.getUsername());

            return ResponseEntity.ok(new JwtResponse(token));
        } catch (BadCredentialsException e) {
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




package com.militaryservices.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.militaryservices.app.dto.SoldDto;
import com.militaryservices.app.dto.SoldierDto;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.service.SerOfUnitService;
import com.militaryservices.app.service.SoldierService;
import com.militaryservices.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Optional;

@RestController
public class SoldiersController {

    @Autowired
    private SoldierService soldierService;
    @Autowired
    private SerOfUnitService serOfUnitService;
    @Autowired
    private UserService userService;
    @Autowired
    private JwtUtil jwtUtil;

    public SoldiersController() {

    }

    @GetMapping("/getSoldiers")
    public ResponseEntity<?> getSoldiers(HttpServletRequest request) {
        if(jwtUtil.validateRequest(request))
            return ResponseEntity.ok(soldierService.findAll(jwtUtil.extractUsername(request)));
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @GetMapping("/calc")
    public ResponseEntity<?> calculateNewServices(HttpServletRequest request) {
        if(jwtUtil.validateRequest(request)) {
            soldierService.calculateServices(jwtUtil.extractUsername(request));
            return ResponseEntity.ok("");
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @GetMapping("/getServices")
    public ResponseEntity<?> getServices(HttpServletRequest request) {

        if(jwtUtil.validateRequest(request)) {
            return ResponseEntity.ok(serOfUnitService.getAllServices());
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");

    }

    @GetMapping("/getNameOfUnit")
    public ResponseEntity<?> getNameOfUnit(HttpServletRequest request) {

        if(jwtUtil.validateRequest(request)) {
            Optional<com.militaryservices.app.entity.User> optionalUser = userService.findUser(jwtUtil.extractUsername(request));

            return ResponseEntity.ok(optionalUser.get().getSoldier().getUnit().getNameOfUnit());
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @PostMapping("/getSoldier")
    public ResponseEntity<?> getSoldier(HttpServletRequest request,@RequestBody String sold) {

        JsonNode jsonNode = getJsonNode(sold);
        String token = jsonNode.get("token").asText();
        if(jwtUtil.validateRequest(request)) {
            SoldierDto soldier = new SoldierDto(jsonNode.get("name").asText(), jsonNode.get("surname").asText(),
                    jsonNode.get("situation").asText(), jsonNode.get("active").asText());
            soldier.setToken(token);
            soldier.setDate(new Date());
            return ResponseEntity.ok(soldier);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @PostMapping("/changeSoldSituation")
    public ResponseEntity<?> changeSoldierSituation(HttpServletRequest request,@RequestBody String sold) {

        JsonNode jsonNode = getJsonNode(sold);
        String token = jsonNode.get("token").asText();
        int soldId = Integer.valueOf(jwtUtil.extractUsername(token));
        if(jwtUtil.validateRequest(request)) {
            SoldDto soldDto = new SoldDto(soldId,jsonNode.get("name").asText(), jsonNode.get("surname").asText(),
                    jsonNode.get("situation").asText(), jsonNode.get("active").asText());
            soldierService.updateSoldier(soldDto);
            return ResponseEntity.ok("Soldier updated successfully.");
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    private JsonNode getJsonNode(String json) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode jsonNode;
        try {
            jsonNode = objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return jsonNode;
    }

}

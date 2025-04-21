package com.militaryservices.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.militaryservices.app.dto.SoldDto;
import com.militaryservices.app.dto.SoldierDto;
import com.militaryservices.app.dto.SoldierPreviousServiceDto;
import com.militaryservices.app.dto.SoldierUnitDto;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.security.SanitizationUtil;
import com.militaryservices.app.security.UserPermission;
import com.militaryservices.app.service.SerOfUnitService;
import com.militaryservices.app.service.SoldierService;
import com.militaryservices.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
    @Autowired
    private UserPermission userPermission;

    public SoldiersController() {

    }

    @GetMapping("/getSoldiers")
    public ResponseEntity<?> getSoldiers(HttpServletRequest request) {
        if(jwtUtil.validateRequest(request)) {
            List<SoldierDto> soldiers = soldierService.findAll(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)));
            // Sanitize the data which are String.
            soldiers = soldiers.stream()
                    .map(soldier -> new SoldierDto(
                            soldier.getToken(),
                            SanitizationUtil.sanitize(soldier.getName()),
                            SanitizationUtil.sanitize(soldier.getSurname()),
                            SanitizationUtil.sanitize(soldier.getSituation()),
                            SanitizationUtil.sanitize(soldier.getActive()),
                            SanitizationUtil.sanitize(soldier.getService()),
                            soldier.extractDate(),
                            SanitizationUtil.sanitize(soldier.getArmed())
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(soldiers);
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @GetMapping("/getPreviousCalculation")
    public ResponseEntity<?> getPreviousCalculation(HttpServletRequest request,@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date prevDate) {
        if(jwtUtil.validateRequest(request)) {
            List<SoldierPreviousServiceDto> soldiers = soldierService.findPreviousCalculation(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)),prevDate);
            // Sanitize the data which are String.
            soldiers = soldiers.stream()
                    .map(soldier -> new SoldierPreviousServiceDto(
                            soldier.getToken(),
                            SanitizationUtil.sanitize(soldier.getSoldierRegistrationNumber()),
                            SanitizationUtil.sanitize(soldier.getName()),
                            SanitizationUtil.sanitize(soldier.getSurname()),
                            SanitizationUtil.sanitize(soldier.getSituation()),
                            SanitizationUtil.sanitize(soldier.getActive()),
                            SanitizationUtil.sanitize(soldier.getService()),
                            soldier.getDate(),
                            soldier.getArmed(),
                            SanitizationUtil.sanitize(soldier.getDischarged())
                    ))
                    .collect(Collectors.toList());

            return ResponseEntity.ok(soldiers);
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @GetMapping("/calc")
    public ResponseEntity<?> calculateNewServices(HttpServletRequest request) {
        if(jwtUtil.validateRequest(request)) {
            soldierService.calculateServices(SanitizationUtil.sanitize(jwtUtil.extractUsername(request))); // Sanitize username
            return ResponseEntity.ok("");
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @GetMapping("/getServices")
    public ResponseEntity<?> getServices(HttpServletRequest request) {

        if(jwtUtil.validateRequest(request)) {
            Optional<User> user = userService.findUser(jwtUtil.extractUsername(request));
            return ResponseEntity.ok(serOfUnitService.getAllServices(user.get().getSoldier().getUnit()));
        }
        else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @GetMapping("/getNameOfUnit")
    public ResponseEntity<?> getNameOfUnit(HttpServletRequest request) {

        if(jwtUtil.validateRequest(request)) {
            Optional<com.militaryservices.app.entity.User> optionalUser = userService.findUser(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)));

            return ResponseEntity.ok(SanitizationUtil.sanitize(optionalUser.get().getSoldier().getUnit().getNameOfUnit())); // Sanitize name of unit data
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @PostMapping("/getSoldier")
    public ResponseEntity<?> getSoldier(HttpServletRequest request,@RequestBody String sold) {

        JsonNode jsonNode = getJsonNode(sold);
        String token = jsonNode.get("token").asText();
        int id = Integer.valueOf(jwtUtil.extractUsername(token));
        SoldierUnitDto soldierDto = soldierService.findSoldier(id);
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token,request,soldierDto.getSituation(),soldierDto.getActive());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no rights to access this data.");
        if(jwtUtil.validateRequest(request)) {
            SoldierDto soldier = new SoldierDto(SanitizationUtil.sanitize(jsonNode.get("name").asText()), SanitizationUtil.sanitize(jsonNode.get("surname").asText()),
                    SanitizationUtil.sanitize(soldierDto.getSituation()), SanitizationUtil.sanitize(soldierDto.getActive()));
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
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token,request,jsonNode.get("situation").asText(),jsonNode.get("active").asText());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("You have no rights to access this data.");
        if(jwtUtil.validateRequest(request)) {
            int soldId = Integer.valueOf(jwtUtil.extractUsername(token));
            SoldDto soldDto = new SoldDto(soldId,SanitizationUtil.sanitize(jsonNode.get("name").asText()), SanitizationUtil.sanitize(jsonNode.get("surname").asText())
                    , jsonNode.get("situation").asText(), jsonNode.get("active").asText());
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

package com.militaryservices.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.enums.Discharged;
import com.militaryservices.app.enums.MessageKey;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.security.RoleExpressions;
import com.militaryservices.app.security.SanitizationUtil;
import com.militaryservices.app.security.UserPermission;
import com.militaryservices.app.service.MessageService;
import com.militaryservices.app.service.SerOfUnitService;
import com.militaryservices.app.service.SoldierService;
import com.militaryservices.app.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@RestController
@PreAuthorize(RoleExpressions.SOLDIER)
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
    @Autowired
    private MessageService messageService;

    public SoldiersController() {

    }

    @GetMapping("/getSoldiers")
    public ResponseEntity<?> getSoldiers(HttpServletRequest request) {
        List<SoldierDto> soldiers = soldierService.findAll(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)));
        // Sanitize the data which are String.
        soldiers = soldiers.stream()
                .map(soldier -> new SoldierDto(
                        soldier.getToken(),
                        SanitizationUtil.sanitize(soldier.getCompany()),
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

    @GetMapping("/getSoldiersOfUnit")
    public ResponseEntity<?> getSoldiersOfUnit(HttpServletRequest request) {
        List<SoldierPersonalDataDto> soldiers = soldierService.loadSoldiers(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)));
        // Sanitize the data.
        soldiers = soldiers.stream()
                .map(soldier -> new SoldierPersonalDataDto(
                        soldier.getToken(),
                        soldier.getSoldierRegistrationNumber(),
                        SanitizationUtil.sanitize(soldier.getCompany()),
                        SanitizationUtil.sanitize(soldier.getName()),
                        SanitizationUtil.sanitize(soldier.getSurname()),
                        SanitizationUtil.sanitize(soldier.getSituation()),
                        SanitizationUtil.sanitize(soldier.getActive()),
                        SanitizationUtil.sanitize(soldier.getDischarged()),
                        SanitizationUtil.sanitize(soldier.getPatronymic()),
                        SanitizationUtil.sanitize(soldier.getMatronymic()),
                        SanitizationUtil.sanitize(soldier.getMobilePhone()),
                        SanitizationUtil.sanitize(soldier.getCity()),
                        SanitizationUtil.sanitize(soldier.getAddress())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(soldiers);
    }

    @GetMapping("/getSoldierByRegistrationNumber")
    public ResponseEntity<?> getSoldierByRegistrationNumber(HttpServletRequest request,@RequestParam("regnumb") String registrationNumber) {

        List<SoldierPersonalDataDto> soldiers = soldierService.findSoldiersByRegistrationNumber(registrationNumber);
        // Sanitize the data.
        soldiers = soldiers.stream()
                .map(soldier -> new SoldierPersonalDataDto(
                        soldier.getToken(),
                        soldier.getSoldierRegistrationNumber(),
                        SanitizationUtil.sanitize(soldier.getCompany()),
                        SanitizationUtil.sanitize(soldier.getName()),
                        SanitizationUtil.sanitize(soldier.getSurname()),
                        SanitizationUtil.sanitize(soldier.getSituation()),
                        SanitizationUtil.sanitize(soldier.getActive()),
                        SanitizationUtil.sanitize(soldier.getDischarged()),
                        SanitizationUtil.sanitize(soldier.getPatronymic()),
                        SanitizationUtil.sanitize(soldier.getMatronymic()),
                        SanitizationUtil.sanitize(soldier.getMobilePhone()),
                        SanitizationUtil.sanitize(soldier.getCity()),
                        SanitizationUtil.sanitize(soldier.getAddress())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(soldiers);
    }

    @GetMapping("/getServicesOfSoldier")
    public  ResponseEntity<?> getServicesOfSoldier(HttpServletRequest request,@RequestParam("soldierToken") String soldierToken) {
        Optional<User> user = userService.findUser(jwtUtil.extractUsername(request));
        String soldierId = jwtUtil.extractUsername(soldierToken);
        List<ServiceDto> services = soldierService.findServicesOfSoldier(user.get().getSoldier().getUnit(),Integer.parseInt(soldierId));
        // Sanitize the data
        services = services.stream()
                .map(service -> new ServiceDto(
                        service.getId(),
                        SanitizationUtil.sanitize(service.getService()),
                        service.getServiceDate(),
                        SanitizationUtil.sanitize(service.getArmed()),
                        SanitizationUtil.sanitize(service.getDescription()),
                        SanitizationUtil.sanitize(service.getShift())
                )).collect(Collectors.toList());

        return ResponseEntity.ok(services);
    }

    @GetMapping("/dischargeSoldier")
    public ResponseEntity<?> dischargeSoldier(HttpServletRequest request,@RequestParam("soldierToken") String soldierToken) {
        Optional<User> user = userService.findUser(jwtUtil.extractUsername(request));
        Unit unit = user.get().getSoldier().getUnit();
        String soldierId = jwtUtil.extractUsername(soldierToken);
        boolean result = soldierService.dischargeSoldier(Integer.parseInt(soldierId),unit);
        return result ? ResponseEntity.ok(messageService.getMessage(MessageKey.DISCHARGE_SOLDIER_SUCCESSFUL.key(), Locale.ENGLISH)) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.DISCHARGE_SOLDIER_NOT_PERMITTED.key(), Locale.ENGLISH));
    }

    @GetMapping("/getFirstCalcDate")
    public ResponseEntity<?> getFirstCalcDate(HttpServletRequest request) {
        Date dateOfFirstCalc = soldierService.getDateByCalculationNumber(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)),1);
        return ResponseEntity.ok(dateOfFirstCalc);
    }

    @GetMapping("/getPreviousCalculation")
    public ResponseEntity<?> getPreviousCalculation(HttpServletRequest request,@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date prevDate) {
        List<SoldierPreviousServiceDto> soldiers = soldierService.findPreviousCalculation(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)),prevDate);
        // Sanitize the data which are String.
        soldiers = soldiers.stream()
                .map(soldier -> new SoldierPreviousServiceDto(
                        soldier.getToken(),
                        SanitizationUtil.sanitize(soldier.getSoldierRegistrationNumber()),
                        SanitizationUtil.sanitize(soldier.getCompany()),
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

    @GetMapping("/calc")
    public ResponseEntity<?> calculateNewServices(HttpServletRequest request) {
        soldierService.calculateServices(SanitizationUtil.sanitize(jwtUtil.extractUsername(request))); // Sanitize username
        return ResponseEntity.ok(messageService.getMessage(MessageKey.NEW_SERVICES_CALCULATED.key(),Locale.ENGLISH));
    }

    @GetMapping("/getServices")
    public ResponseEntity<?> getServices(HttpServletRequest request,@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date prevDate) {
        Optional<User> user = userService.findUser(jwtUtil.extractUsername(request));
        return ResponseEntity.ok(serOfUnitService.getAllServices(user.get().getSoldier().getUnit(),prevDate));
    }

    @GetMapping("/getNameOfUnit")
    public ResponseEntity<?> getNameOfUnit(HttpServletRequest request) {
        Optional<com.militaryservices.app.entity.User> optionalUser = userService.findUser(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)));
        return ResponseEntity.ok(SanitizationUtil.sanitize(optionalUser.get().getSoldier().getUnit().getNameOfUnit())); // Sanitize name of unit data
    }

    @PostMapping("/getSoldier")
    public ResponseEntity<?> getSoldier(HttpServletRequest request,@RequestBody String sold) {

        JsonNode jsonNode = getJsonNode(sold);
        String token = jsonNode.get("token").asText();
        int id = Integer.valueOf(jwtUtil.extractUsername(token));
        SoldierUnitDto soldierDto = soldierService.findSoldier(id);
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token,request,soldierDto.getSituation(),soldierDto.getActive());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));

        SoldierDto soldier = new SoldierDto(SanitizationUtil.sanitize(jsonNode.get("name").asText()), SanitizationUtil.sanitize(jsonNode.get("surname").asText()),
                SanitizationUtil.sanitize(soldierDto.getSituation()), SanitizationUtil.sanitize(soldierDto.getActive()));
        soldier.setToken(token);
        soldier.setDate(new Date());
        return ResponseEntity.ok(soldier);
    }

    @PostMapping("/saveNewSoldier")
    public ResponseEntity<?> saveNewSoldier(HttpServletRequest request,@Valid @RequestBody SoldierPersonalDataDto soldierDto) {
        Optional<User> user = userService.findUser(jwtUtil.extractUsername(request));
        Unit unit = user.get().getSoldier().getUnit();
        SoldierPersonalDataDto soldier = new SoldierPersonalDataDto();
        soldier.setSoldierRegistrationNumber(SanitizationUtil.sanitize(soldierDto.getSoldierRegistrationNumber()));
        soldier.setCompany(SanitizationUtil.sanitize(soldierDto.getCompany()));
        soldier.setName(SanitizationUtil.sanitize(soldierDto.getName()));
        soldier.setSurname(SanitizationUtil.sanitize(soldierDto.getSurname()));
        soldier.setDischarged(Discharged.getDischarged(false));
        soldier.setSituation(SanitizationUtil.sanitize(soldierDto.getSituation()));
        soldier.setActive(SanitizationUtil.sanitize(soldierDto.getActive()));
        soldier.setPatronymic(SanitizationUtil.sanitize(soldierDto.getPatronymic()));
        soldier.setMatronymic(SanitizationUtil.sanitize(soldierDto.getMatronymic()));
        soldier.setMobilePhone(SanitizationUtil.sanitize(soldierDto.getMobilePhone()));
        soldier.setCity(SanitizationUtil.sanitize(soldierDto.getCity()));
        soldier.setAddress(SanitizationUtil.sanitize(soldierDto.getAddress()));
        soldierService.saveNewSoldier(soldier,unit);
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SOLDIER_SAVED.key(), Locale.ENGLISH));
    }

    @PostMapping("/saveNewServices")
    @PreAuthorize(RoleExpressions.COMMANDER)
    public  ResponseEntity<?> saveNewServices(HttpServletRequest request,@RequestBody String payload) {
        JsonNode jsonNode = getJsonNode(payload);
        Optional<User> user = userService.findUser(jwtUtil.extractUsername(request));
        int numberOfGuards = jsonNode.get("selectedNumberOfGuards").asInt();
        String nameOfService = jsonNode.get("nameOfService").asText();
        String armedStatus = jsonNode.get("armed").asText();
        String description = jsonNode.get("description").asText();
        String shift = jsonNode.get("shift").asText();
        Soldier soldier = user.get().getSoldier();
        Unit unit = soldier.getUnit();
        ServiceOfUnit serviceOfUnit = new ServiceOfUnit(nameOfService, armedStatus, soldier.getCompany(), description, shift, unit);
        if(!serOfUnitService.checkIfAllowed(unit,numberOfGuards,serviceOfUnit))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageService.getMessage(MessageKey.ADD_SERVICES_REJECTED.key(),Locale.ENGLISH));

        IntStream.range(0, numberOfGuards)
                .mapToObj(i -> {
                    // Create a new instance of ServiceOfUnit if you want separate instances
                    ServiceOfUnit newService = new ServiceOfUnit(nameOfService, armedStatus, soldier.getCompany(), description, shift, unit);
                    newService.setId(null);
                    return newService;
                })
                .forEach(serOfUnitService::saveService);
        return ResponseEntity.ok(messageService.getMessage(MessageKey.ADD_SERVICES.key(),Locale.ENGLISH));
    }

    @PostMapping("/deleteServices")
    @PreAuthorize(RoleExpressions.COMMANDER)
    public  ResponseEntity<?> deleteServices(HttpServletRequest request,@RequestBody String payload) {
        JsonNode jsonNode = getJsonNode(payload);
        soldierService.deleteServices(jsonNode.get("ids"));
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SERVICES_DELETED.key(),Locale.ENGLISH));
    }

    @PostMapping("/changeSoldSituation")
    public ResponseEntity<?> changeSoldierSituation(HttpServletRequest request,@RequestBody String sold) {

        JsonNode jsonNode = getJsonNode(sold);
        String token = jsonNode.get("token").asText();
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token,request,jsonNode.get("situation").asText(),jsonNode.get("active").asText());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));
        int soldId = Integer.valueOf(jwtUtil.extractUsername(token));
        SoldDto soldDto = new SoldDto(soldId,SanitizationUtil.sanitize(jsonNode.get("name").asText()), SanitizationUtil.sanitize(jsonNode.get("surname").asText())
                , jsonNode.get("situation").asText(), jsonNode.get("active").asText());
        soldierService.updateSoldier(soldDto);
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SOLDIER_UPDATED.key(),Locale.ENGLISH));
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

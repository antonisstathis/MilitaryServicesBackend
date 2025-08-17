package com.militaryservices.app.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.enums.Discharged;
import com.militaryservices.app.enums.MessageKey;
import com.militaryservices.app.enums.StatisticalData;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.security.RoleExpressions;
import com.militaryservices.app.security.SanitizationUtil;
import com.militaryservices.app.security.UserPermission;
import com.militaryservices.app.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@Validated
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
    @Autowired
    private UnitService unitService;

    public SoldiersController() {

    }

    @GetMapping("/getSoldiers")
    public ResponseEntity<?> getSoldiers(HttpServletRequest request, @RequestParam("isPersonnel") boolean isPersonnel) {
        List<SoldierDto> soldiers = soldierService.findAll(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)), isPersonnel);
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
    public ResponseEntity<?> getSoldiersOfUnit(HttpServletRequest request, @RequestParam("isPersonnel") boolean isPersonnel) {
        List<SoldierPersonalDataDto> soldiers = soldierService.loadSoldiers(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)),isPersonnel);
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
                        SanitizationUtil.sanitize(soldier.getAddress()),
                        soldier.isPersonnel(),
                        SanitizationUtil.sanitize(soldier.getGroup())
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
                        SanitizationUtil.sanitize(soldier.getAddress()),
                        soldier.isPersonnel(),
                        SanitizationUtil.sanitize(soldier.getGroup())
                ))
                .collect(Collectors.toList());

        return ResponseEntity.ok(soldiers);
    }

    @GetMapping("/getServicesOfSoldier")
    public  ResponseEntity<?> getServicesOfSoldier(HttpServletRequest request,@RequestParam("soldierToken") String soldierToken) {
        UserDto user = userService.findUser(jwtUtil.extractUsername(request));
        String soldierId = jwtUtil.extractUsername(soldierToken);
        List<ServiceDto> services = soldierService.findServicesOfSoldier(Integer.parseInt(soldierId));
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
        UserDto user = userService.findUser(jwtUtil.extractUsername(request));
        String soldierId = jwtUtil.extractUsername(soldierToken);
        boolean result = soldierService.dischargeSoldier(Integer.parseInt(soldierId));
        return result ? ResponseEntity.ok(messageService.getMessage(MessageKey.DISCHARGE_SOLDIER_SUCCESSFUL.key(), Locale.ENGLISH)) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.DISCHARGE_SOLDIER_NOT_PERMITTED.key(), Locale.ENGLISH));
    }

    @GetMapping("/getFirstCalcDate")
    public ResponseEntity<?> getFirstCalcDate(HttpServletRequest request) {
        Date dateOfFirstCalc = soldierService.getDateByCalculationNumber(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)),1);
        return ResponseEntity.ok(dateOfFirstCalc);
    }

    @GetMapping("/getLastCalcDate")
    public ResponseEntity<?> getLastCalcDate(HttpServletRequest request, @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userService.findUser(jwtUtil.extractUsername(request));
        Date dateOfLastCalculation = soldierService.getDateOfLastCalculation(user,isPersonnel);
        return ResponseEntity.ok(dateOfLastCalculation);
    }

    @GetMapping("/getPreviousCalculation")
    public ResponseEntity<?> getPreviousCalculation(HttpServletRequest request,@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") Date prevDate,@RequestParam("isPersonnel") boolean isPersonnel) {
        List<SoldierPreviousServiceDto> soldiers = soldierService.findPreviousCalculation(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)),prevDate,isPersonnel);
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
    public ResponseEntity<?> calculateNewServices(HttpServletRequest request,@RequestParam("lastDate") @DateTimeFormat(pattern = "yyyy-MM-dd") Date lastDate,
                                                  @RequestParam("isPersonnel") boolean isPersonnel){
        soldierService.calculateServices(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)),lastDate,isPersonnel); // Sanitize username
        return ResponseEntity.ok(messageService.getMessage(MessageKey.NEW_SERVICES_CALCULATED.key(),Locale.ENGLISH));
    }

    @GetMapping("/getServices")
    public ResponseEntity<?> getServices(HttpServletRequest request,@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") Date prevDate,
                                         @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userService.findUser(jwtUtil.extractUsername(request));
        return ResponseEntity.ok(serOfUnitService.getAllServices(user,prevDate,isPersonnel));
    }

    @GetMapping("/getNameOfUnit")
    public ResponseEntity<?> getNameOfUnit(HttpServletRequest request) {
        UserDto user = userService.findUser(SanitizationUtil.sanitize(jwtUtil.extractUsername(request)));
        return ResponseEntity.ok(SanitizationUtil.sanitize(unitService.findNameOfUnit(user))); // Sanitize name of unit data
    }

    @GetMapping("/getSoldiersStatistics")
    public ResponseEntity<?> getStatistics(HttpServletRequest request,@RequestParam StatisticalData statisticalDataOption, @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userService.findUser(jwtUtil.extractUsername(request));
        List<SoldierServiceStatDto> soldierServiceStatDtos = soldierService.getSoldierServiceStats(user, statisticalDataOption,isPersonnel);
        return ResponseEntity.ok(soldierServiceStatDtos);
    }

    @PostMapping("/getSoldier")
    public ResponseEntity<?> getSoldier(HttpServletRequest request,@Valid @RequestBody SoldierSelectDto soldDto) {
        String token = soldDto.getToken();
        int id = Integer.valueOf(jwtUtil.extractUsername(token));
        SoldierSelectDto soldierDto = soldierService.findSoldier(id);
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token,request,soldierDto.getSituation(),soldierDto.getActive());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));

        return ResponseEntity.ok(soldierDto);
    }

    @PostMapping("/changeSoldSituation")
    public ResponseEntity<?> changeSoldierSituation(HttpServletRequest request,@Valid @RequestBody SoldierSelectDto soldDto) {
        String token = soldDto.getToken();
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token, request, soldDto.getSituation(), soldDto.getActive());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));
        SoldierSelectDto soldierDto = new SoldierSelectDto(soldDto.getToken(), SanitizationUtil.sanitize(soldDto.getName()), SanitizationUtil.sanitize(soldDto.getSurname())
                , SanitizationUtil.sanitize(soldDto.getSituation()), SanitizationUtil.sanitize(soldDto.getActive()), SanitizationUtil.sanitize(soldDto.getGroup()));
        soldierService.updateSoldier(soldierDto);
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SOLDIER_UPDATED.key(),Locale.ENGLISH));
    }

    @PostMapping("/saveNewSoldier")
    public ResponseEntity<?> saveNewSoldier(HttpServletRequest request,@Valid @RequestBody SoldierPersonalDataDto soldierDto) {
        UserDto user = userService.findUser(jwtUtil.extractUsername(request));
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
        soldier.setGroup(SanitizationUtil.sanitize(soldierDto.getGroup()));
        soldierService.saveNewSoldier(soldier,user);
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SOLDIER_SAVED.key(), Locale.ENGLISH));
    }

    @PostMapping("/saveNewServices")
    @PreAuthorize(RoleExpressions.COMMANDER)
    public ResponseEntity<?> saveNewServices(HttpServletRequest request, @RequestBody @Valid ServiceOfUnitDto dto,@RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userService.findUser(jwtUtil.extractUsername(request));
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!serOfUnitService.checkIfAllowed(user, dto.getNumberOfGuards(), dto, isPersonnel))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageService.getMessage(MessageKey.ADD_SERVICES_REJECTED.key(), Locale.ENGLISH));

        serOfUnitService.saveService(dto, user, isPersonnel);

        return ResponseEntity.ok(messageService.getMessage(MessageKey.ADD_SERVICES.key(), Locale.ENGLISH));
    }

    @PostMapping("/deleteServices")
    @PreAuthorize(RoleExpressions.COMMANDER)
    public  ResponseEntity<?> deleteServices(HttpServletRequest request,@RequestBody String payload) {
        JsonNode jsonNode = getJsonNode(payload);
        soldierService.deleteServices(jsonNode.get("ids"));
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SERVICES_DELETED.key(),Locale.ENGLISH));
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

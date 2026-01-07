package com.militaryservices.app.controller;

import com.militaryservices.app.dto.*;
import com.militaryservices.app.enums.Discharged;
import com.militaryservices.app.enums.MessageKey;
import com.militaryservices.app.enums.StatisticalData;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.security.RoleExpressions;
import com.militaryservices.app.security.UserPermission;
import com.militaryservices.app.service.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.*;
import org.slf4j.Logger;

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
    @Autowired
    private UserRequestHelper userRequestHelper;
    private static final Logger logger = LoggerFactory.getLogger(SoldiersController.class);


    public SoldiersController() {

    }

    @GetMapping("/api/getSoldiers")
    public ResponseEntity<?> getSoldiers(HttpServletRequest request, @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto userDto = userRequestHelper.getUserFromRequest(request);
        List<SoldierDto> soldiers = soldierService.findAll(userDto, isPersonnel);

        return ResponseEntity.ok(soldiers);
    }

    @GetMapping("/api/getSoldiersOfUnit")
    public ResponseEntity<?> getSoldiersOfUnit(HttpServletRequest request, @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto userDto = userRequestHelper.getUserFromRequest(request);
        List<SoldierPersonalDataDto> soldiers = soldierService.loadSoldiers(userDto,isPersonnel);

        return ResponseEntity.ok(soldiers);
    }

    @GetMapping("/api/getSoldierByRegistrationNumber")
    public ResponseEntity<?> getSoldierByRegistrationNumber(HttpServletRequest request,@RequestParam("regnumb") String registrationNumber) {
        UserDto userDto = userRequestHelper.getUserFromRequest(request);
        List<SoldierPersonalDataDto> soldiers = soldierService.findSoldiersByRegistrationNumber(registrationNumber,userDto);

        return ResponseEntity.ok(soldiers);
    }

    @GetMapping("/api/getServicesOfSoldier")
    public ResponseEntity<?> getServicesOfSoldier(HttpServletRequest request,@RequestParam("soldierToken") String soldierToken) {
        if(!jwtUtil.isTokenValid(soldierToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));
        String soldierId = jwtUtil.extractUsername(soldierToken);
        List<ServiceDto> services = soldierService.findServicesOfSoldier(Integer.parseInt(soldierId));

        return ResponseEntity.ok(services);
    }

    @GetMapping("/api/dischargeSoldier")
    public ResponseEntity<?> dischargeSoldier(HttpServletRequest request,@RequestParam("soldierToken") String soldierToken) {
        if(!jwtUtil.isTokenValid(soldierToken))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));
        String soldierId = jwtUtil.extractUsername(soldierToken);
        boolean result = soldierService.dischargeSoldier(Integer.parseInt(soldierId));
        return result ? ResponseEntity.ok(messageService.getMessage(MessageKey.DISCHARGE_SOLDIER_SUCCESSFUL.key(), Locale.ENGLISH)) :
                ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.DISCHARGE_SOLDIER_NOT_PERMITTED.key(), Locale.ENGLISH));
    }

    @GetMapping("/api/getFirstCalcDate")
    public ResponseEntity<?> getFirstCalcDate(HttpServletRequest request, @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto userDto = userRequestHelper.getUserFromRequest(request);
        LocalDate dateOfFirstCalc = soldierService.getDateByCalculationNumber(userDto,1, isPersonnel);
        return ResponseEntity.ok(dateOfFirstCalc);
    }

    @GetMapping("/api/getLastCalcDate")
    public ResponseEntity<?> getLastCalcDate(HttpServletRequest request, @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userRequestHelper.getUserFromRequest(request);
        LocalDate dateOfLastCalculation = soldierService.getDateOfLastCalculation(user,isPersonnel);
        return ResponseEntity.ok(dateOfLastCalculation);
    }

    @GetMapping("/api/getPreviousCalculation")
    public ResponseEntity<?> getPreviousCalculation(HttpServletRequest request,@RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate prevDate,@RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto userDto = userRequestHelper.getUserFromRequest(request);
        List<SoldierPreviousServiceDto> soldiers = soldierService.findPreviousCalculation(userDto,prevDate,isPersonnel);

        return ResponseEntity.ok(soldiers);
    }

    @GetMapping("/api/calc")
    public ResponseEntity<?> calculateNewServices(HttpServletRequest request,@RequestParam("lastDate") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate lastDate,
                                                  @RequestParam("isPersonnel") boolean isPersonnel){
        UserDto user = userRequestHelper.getUserFromRequest(request);
        long start = System.nanoTime();  // start timer
        soldierService.calculateServices(user,lastDate,isPersonnel);
        long end = System.nanoTime();    // end timer
        long elapsedMs = (end - start) / 1_000_000; // convert to ms
        return ResponseEntity.ok(messageService.getMessage(MessageKey.NEW_SERVICES_CALCULATED.key(),Locale.ENGLISH));
    }

    @GetMapping("/api/getServices")
    public ResponseEntity<?> getServices(HttpServletRequest request,@RequestParam(value = "date", required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate prevDate,
                                         @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userRequestHelper.getUserFromRequest(request);
        return ResponseEntity.ok(serOfUnitService.getAllServices(user,prevDate,isPersonnel));
    }

    @GetMapping("/api/getNameOfUnit")
    public ResponseEntity<?> getNameOfUnit(HttpServletRequest request) {
        UserDto user = userRequestHelper.getUserFromRequest(request);
        return ResponseEntity.ok(unitService.findNameOfUnit(user));
    }

    @GetMapping("/api/getSoldiersStatistics")
    public ResponseEntity<?> getStatistics(HttpServletRequest request,@RequestParam StatisticalData statisticalDataOption, @RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userRequestHelper.getUserFromRequest(request);
        List<SoldierServiceStatDto> soldierServiceStatDtos = soldierService.getSoldierServiceStats(user, statisticalDataOption,isPersonnel);
        return ResponseEntity.ok(soldierServiceStatDtos);
    }

    @PostMapping("/api/getSoldier")
    public ResponseEntity<?> getSoldier(HttpServletRequest request,@Valid @RequestBody SoldierSelectDto soldDto) {
        String token = soldDto.getToken();
        if(!jwtUtil.isTokenValid(token))
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));
        int id = Integer.valueOf(jwtUtil.extractUsername(token));
        SoldierSelectDto soldierDto = soldierService.findSoldier(id);
        UserDto userDto = userRequestHelper.getUserFromRequest(request);
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token,userDto,soldierDto.getSituation(),soldierDto.getActive());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));

        return ResponseEntity.ok(soldierDto);
    }

    @PostMapping("/api/changeSoldSituation")
    public ResponseEntity<?> changeSoldierSituation(HttpServletRequest request,@Valid @RequestBody SoldierSelectDto soldDto) {
        UserDto userDto = userRequestHelper.getUserFromRequest(request);
        String token = soldDto.getToken();
        boolean userHasAccess = userPermission.checkIfUserHasAccess(token, userDto, soldDto.getSituation(), soldDto.getActive());
        if(!userHasAccess)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(messageService.getMessage(MessageKey.UNAUTHORIZED.key(),Locale.ENGLISH));
        SoldierSelectDto soldierDto = new SoldierSelectDto(soldDto.getToken(), soldDto.getName(), soldDto.getSurname()
                , soldDto.getSituation(), soldDto.getActive(), soldDto.getGroup());
        soldierService.updateSoldier(soldierDto);
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SOLDIER_UPDATED.key(),Locale.ENGLISH));
    }

    @PostMapping("/api/saveNewSoldier")
    public ResponseEntity<?> saveNewSoldier(HttpServletRequest request,@Valid @RequestBody SoldierPersonalDataDto soldierDto) {
        UserDto user = userRequestHelper.getUserFromRequest(request);
        SoldierPersonalDataDto soldier = new SoldierPersonalDataDto();
        soldier.setSoldierRegistrationNumber(soldierDto.getSoldierRegistrationNumber());
        soldier.setCompany(soldierDto.getCompany());
        soldier.setName(soldierDto.getName());
        soldier.setSurname(soldierDto.getSurname());
        soldier.setDischarged(Discharged.getDischarged(false));
        soldier.setSituation(soldierDto.getSituation());
        soldier.setActive(soldierDto.getActive());
        soldier.setPatronymic(soldierDto.getPatronymic());
        soldier.setMatronymic(soldierDto.getMatronymic());
        soldier.setMobilePhone(soldierDto.getMobilePhone());
        soldier.setCity(soldierDto.getCity());
        soldier.setAddress(soldierDto.getAddress());
        soldier.setGroup(soldierDto.getGroup());
        boolean isSaved = soldierService.saveNewSoldier(soldier,user);

        if(isSaved)
            return ResponseEntity.ok(messageService.getMessage(MessageKey.SOLDIER_SAVED.key(), Locale.ENGLISH));
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageService.getMessage(MessageKey.REG_NUMBER_ALREADY_EXISTS.key(), Locale.ENGLISH));
    }

    @PostMapping("/api/saveNewServices")
    @PreAuthorize(RoleExpressions.COMMANDER)
    public ResponseEntity<?> saveNewServices(HttpServletRequest request, @RequestBody @Valid ServiceOfUnitDto dto,@RequestParam("isPersonnel") boolean isPersonnel) {
        UserDto user = userRequestHelper.getUserFromRequest(request);
        if (user == null)
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();

        if (!serOfUnitService.checkIfAllowed(user, dto.getNumberOfGuards(), dto, isPersonnel, dto.getGroup()))
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(messageService.getMessage(MessageKey.ADD_SERVICES_REJECTED.key(), Locale.ENGLISH));

        serOfUnitService.saveService(dto, user, isPersonnel);

        return ResponseEntity.ok(messageService.getMessage(MessageKey.ADD_SERVICES.key(), Locale.ENGLISH));
    }

    @PostMapping("/api/deleteServices")
    @PreAuthorize(RoleExpressions.COMMANDER)
    public  ResponseEntity<?> deleteServices(HttpServletRequest request,@RequestBody List<Long> ids) {
        soldierService.deleteServices(ids);
        return ResponseEntity.ok(messageService.getMessage(MessageKey.SERVICES_DELETED.key(),Locale.ENGLISH));
    }

    @DeleteMapping("/api/deleteServicesAfterDate")
    @PreAuthorize(RoleExpressions.COMMANDER)
    public ResponseEntity<?> deleteServicesAfterDate(HttpServletRequest request,
            @RequestParam("date") @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate date,
            @RequestParam("isPersonnel") boolean isPersonnel) {

        UserDto user = userRequestHelper.getUserFromRequest(request);

        if(soldierService.deleteServicesAfterDate(user, date, isPersonnel))
            return ResponseEntity.ok(messageService.getMessage(MessageKey.SERVICES_DELETED.key(), Locale.ENGLISH));
        else
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("");
    }

}

package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.enums.StatisticalData;

import java.util.Date;
import java.util.List;

public interface SoldierService {
    List<SoldierDto> findAll(UserDto userDto,boolean isPersonnel);

    List<SoldierPersonalDataDto> loadSoldiers(UserDto userDto,boolean isPersonnel);

    List<SoldierPersonalDataDto> findSoldiersByRegistrationNumber(String registrationNumber);

    List<SoldierPreviousServiceDto> findPreviousCalculation(UserDto userDto, Date date,boolean isPersonnel);

    Date getDateByCalculationNumber(UserDto userDto,int calculation);

    void calculateServices(UserDto user,Date lastDate,boolean isPersonnel);

    void updateSoldier(SoldierSelectDto soldier);

    SoldierSelectDto findSoldier(int id);

    SoldierUnitDto findSoldierUnit(int id);

    List<ServiceDto> findServicesOfSoldier(int soldierId);

    boolean dischargeSoldier(int soldierId);

    List<SoldierServiceStatDto> getSoldierServiceStats(UserDto user, StatisticalData caseType, boolean isPersonnel);

    void deleteServices(JsonNode services);

    Date getDateOfLastCalculation(UserDto user,boolean isPersonnel);

    void saveNewSoldier(SoldierPersonalDataDto soldier, UserDto user);
}

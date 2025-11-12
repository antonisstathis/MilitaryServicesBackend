package com.militaryservices.app.service;

import com.militaryservices.app.dto.*;
import com.militaryservices.app.enums.StatisticalData;

import java.time.LocalDate;
import java.util.List;

public interface SoldierService {
    List<SoldierDto> findAll(UserDto userDto,boolean isPersonnel);

    List<SoldierPersonalDataDto> loadSoldiers(UserDto userDto,boolean isPersonnel);

    List<SoldierPersonalDataDto> findSoldiersByRegistrationNumber(String registrationNumber, UserDto userDto);

    List<SoldierPreviousServiceDto> findPreviousCalculation(UserDto userDto, LocalDate date,boolean isPersonnel);

    LocalDate getDateByCalculationNumber(UserDto userDto, int calculation, boolean isPersonnel);

    void calculateServices(UserDto user,LocalDate lastDate,boolean isPersonnel);

    void updateSoldier(SoldierSelectDto soldier);

    SoldierSelectDto findSoldier(int id);

    SoldierUnitDto findSoldierUnit(int id);

    List<ServiceDto> findServicesOfSoldier(int soldierId);

    boolean dischargeSoldier(int soldierId);

    List<SoldierServiceStatDto> getSoldierServiceStats(UserDto user, StatisticalData caseType, boolean isPersonnel);

    void deleteServices(List<Long> ids);

    LocalDate getDateOfLastCalculation(UserDto user,boolean isPersonnel);

    boolean saveNewSoldier(SoldierPersonalDataDto soldier, UserDto user);
}

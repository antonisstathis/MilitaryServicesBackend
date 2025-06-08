package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.enums.StatisticalData;

import java.util.Date;
import java.util.List;

public interface SoldierService {
    List<SoldierDto> findAll(String username);

    List<SoldierPersonalDataDto> loadSoldiers(String username);

    List<SoldierPersonalDataDto> findSoldiersByRegistrationNumber(String registrationNumber);

    List<SoldierPreviousServiceDto> findPreviousCalculation(String username, Date date);

    Date getDateByCalculationNumber(String username,int calculation);

    void calculateServices(String username,Date lastDate);

    void updateSoldier(SoldDto soldier);

    SoldierUnitDto findSoldier(int id);

    List<ServiceDto> findServicesOfSoldier(Unit unit, int soldierId);

    boolean dischargeSoldier(int soldierId, Unit unit);

    List<SoldierServiceStatDto> getSoldierServiceStats(Unit unit, StatisticalData caseType);

    void deleteServices(JsonNode services);
    void saveNewSoldier(SoldierPersonalDataDto soldier,Unit unit);
}

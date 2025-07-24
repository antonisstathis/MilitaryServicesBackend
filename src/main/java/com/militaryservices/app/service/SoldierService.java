package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.enums.StatisticalData;

import java.util.Date;
import java.util.List;

public interface SoldierService {
    List<SoldierDto> findAll(String username,boolean isPersonnel);

    List<SoldierPersonalDataDto> loadSoldiers(String username,boolean isPersonnel);

    List<SoldierPersonalDataDto> findSoldiersByRegistrationNumber(String registrationNumber);

    List<SoldierPreviousServiceDto> findPreviousCalculation(String username, Date date,boolean isPersonnel);

    Date getDateByCalculationNumber(String username,int calculation);

    void calculateServices(String username,Date lastDate,boolean isPersonnel);

    void updateSoldier(SoldDto soldier);

    SoldierUnitDto findSoldier(int id);

    List<ServiceDto> findServicesOfSoldier(Unit unit, int soldierId);

    boolean dischargeSoldier(int soldierId, Unit unit);

    List<SoldierServiceStatDto> getSoldierServiceStats(Unit unit, StatisticalData caseType, boolean isPersonnel);

    void deleteServices(JsonNode services);

    Date getDateOfLastCalculation(Unit unit,boolean isPersonnel);

    void saveNewSoldier(SoldierPersonalDataDto soldier, Unit unit);
}

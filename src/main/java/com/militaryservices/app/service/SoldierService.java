package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.entity.User;

import java.util.Date;
import java.util.List;

public interface SoldierService {
    List<SoldierDto> findAll(String username);

    List<SoldierPersonalDataDto> loadSoldiers(String username);

    List<SoldierPersonalDataDto> findSoldiersByRegistrationNumber(String registrationNumber);

    List<SoldierPreviousServiceDto> findPreviousCalculation(String username, Date date);

    Date getDateByCalculationNumber(String username,int calculation);

    void deleteById(Soldier soldier);

    void calculateServices(String username);

    void updateSoldier(SoldDto soldier);

    SoldierUnitDto findSoldier(int id);

    List<ServiceDto> findServicesOfSoldier(Unit unit, int soldierId);

    boolean dischargeSoldier(int soldierId, Unit unit);

    void deleteServices(JsonNode services);
    void saveNewSoldier(SoldierPersonalDataDto soldier,Unit unit);
}

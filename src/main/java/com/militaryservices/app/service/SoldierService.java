package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;

import java.util.Date;
import java.util.List;

public interface SoldierService {
    List<SoldierDto> findAll(String username);

    List<SoldierPersonalDataDto> loadSoldiers(String username);

    List<SoldierPreviousServiceDto> findPreviousCalculation(String username, Date date);

    Date getDateByCalculationNumber(String username,int calculation);

    void deleteById(Soldier soldier);

    void calculateServices(String username);

    void updateSoldier(SoldDto soldier);

    SoldierUnitDto findSoldier(int id);

    void deleteServices(JsonNode services);
}

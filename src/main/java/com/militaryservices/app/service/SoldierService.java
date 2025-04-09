package com.militaryservices.app.service;

import com.militaryservices.app.dto.SoldierDto;
import com.militaryservices.app.entity.Soldier;

import java.util.List;

public interface SoldierService {
    List<SoldierDto> findAll(String username);

    void findSoldier(SoldierDto soldier);

    void deleteById(Soldier soldier);

    void calculateServices(String username);
}

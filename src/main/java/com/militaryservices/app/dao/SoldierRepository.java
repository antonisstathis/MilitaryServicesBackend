package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SoldierRepository extends JpaRepository<Soldier, String> {

    List<Soldier> findByUnitAndDischarged(Unit unit,boolean discharged);

    List<Soldier> findBySoldierRegistrationNumberContainingIgnoreCase(String registrationFragment);
}

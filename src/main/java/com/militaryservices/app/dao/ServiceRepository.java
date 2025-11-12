package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Service;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByUnitAndDateAndArmed(Unit unit, LocalDate date,String armed);
    List<Service> findByUnitAndDateAndIsPersonnel(Unit unit, LocalDate date, boolean isPersonnel);

    List<Service> findBySoldierOrderByDateAsc(Soldier soldier);

}

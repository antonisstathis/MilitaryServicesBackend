package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Service;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Date;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByUnitAndDateAndArmed(Unit unit, Date date,String armed);
    List<Service> findByUnitAndDate(Unit unit,Date date);

    List<Service> findBySoldier(Soldier soldier);

}

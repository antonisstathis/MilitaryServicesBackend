package com.militaryservices.app.dao;

import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerOfUnitRepository extends JpaRepository<ServiceOfUnit, Long> {

    List<ServiceOfUnit> findByUnit(Unit unit);

    List<ServiceOfUnit> findByUnitAndArmed(Unit unit, String armed);
}

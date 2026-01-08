package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Service;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;

public interface ServiceRepository extends JpaRepository<Service, Long> {

    List<Service> findByUnitAndDateAndArmed(Unit unit, LocalDate date,String armed);
    List<Service> findByUnitAndDateAndIsPersonnel(Unit unit, LocalDate date, boolean isPersonnel);
    List<Service> findByUnitAndDateAfterAndIsPersonnel(Unit unit, LocalDate date, boolean isPersonnel);
    List<Service> findBySoldierOrderByDateAsc(Soldier soldier);
    List<Service> findByUnitAndIsPersonnel(Unit unit, boolean isPersonnel);

    @Transactional
    @Modifying
    @Query("DELETE FROM Service s WHERE s.unit = :unit AND s.date > :date AND s.isPersonnel = :isPersonnel")
    int deleteByUnitAndDateAfterAndIsPersonnel(Unit unit, LocalDate date, boolean isPersonnel);

}

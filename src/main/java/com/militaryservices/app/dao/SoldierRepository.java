package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SoldierRepository extends JpaRepository<Soldier, String> {

    List<Soldier> findByUnitAndDischargedAndIsPersonnel(Unit unit,boolean discharged,boolean isPersonnel);

    List<Soldier> findByUnitAndDischargedAndIsPersonnelAndSituation(Unit unit,boolean discharged,boolean isPersonnel,String situation);

    List<Soldier> findBySoldierRegistrationNumberContainingIgnoreCase(String registrationFragment);

    @Modifying
    @Transactional
    @Query("UPDATE Soldier s SET s.discharged = :discharged WHERE s.id = :id")
    void updateDischargedStatusById(@Param("id") int id, @Param("discharged") boolean discharged);
}

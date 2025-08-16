package com.militaryservices.app.dao;

import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SerOfUnitRepository extends JpaRepository<ServiceOfUnit, Long> {

    List<ServiceOfUnit> findByUnitAndIsPersonnel(Unit unit, boolean isPersonnel);

    List<ServiceOfUnit> findByUnitAndIsPersonnelAndGroup(Unit unit, boolean isPersonnel, String group);

    List<ServiceOfUnit> findByUnitAndArmedAndIsPersonnel(Unit unit, String armed,boolean isPersonnel);

    @Query("SELECT DISTINCT s.group FROM ServiceOfUnit s WHERE s.unit = :unit and s.isPersonnel =:isPersonnel")
    List<String> findDistinctGroups(@Param("unit") Unit unit, @Param("isPersonnel") boolean isPersonnel);
}

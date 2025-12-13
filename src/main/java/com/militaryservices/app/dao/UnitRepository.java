package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Unit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UnitRepository extends JpaRepository<Unit, Long> {

    boolean existsByNameOfUnit(String nameOfUnit);

    Optional<Unit> findByNameOfUnit(String nameOfUnit);
}

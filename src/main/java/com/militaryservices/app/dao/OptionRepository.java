package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Option;
import com.militaryservices.app.entity.OptionId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OptionRepository extends JpaRepository<Option, OptionId> {

    Optional<Option> findByTableNameAndColumnNameAndOption(String tableName, String columnName, String option);

}

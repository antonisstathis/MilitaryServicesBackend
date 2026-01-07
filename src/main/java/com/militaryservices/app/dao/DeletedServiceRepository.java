package com.militaryservices.app.dao;

import com.militaryservices.app.entity.DeletedService;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DeletedServiceRepository extends JpaRepository<DeletedService, Long> {
}

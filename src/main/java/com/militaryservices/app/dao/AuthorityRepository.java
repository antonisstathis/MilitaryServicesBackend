package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Authority;
import com.militaryservices.app.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthorityRepository extends JpaRepository<Authority, Long> {

    List<Authority> findByUser(User username);
}

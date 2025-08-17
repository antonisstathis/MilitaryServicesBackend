package com.militaryservices.app.service;

import com.militaryservices.app.dao.AuthorityRepository;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.entity.Authority;
import com.militaryservices.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    public List<String> findRolesByUsername(UserDto user) {
        List<Authority> authorities =  authorityRepository.findByUser(new User(user.getUsername(), user.getPassword(), user.isEnabled()));
        return authorities.stream()
                .map(auth -> "ROLE_" + auth.getAuthority().toUpperCase())
                .collect(Collectors.toList());
    }
}

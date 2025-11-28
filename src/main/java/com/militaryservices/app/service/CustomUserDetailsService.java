package com.militaryservices.app.service;

import com.militaryservices.app.dao.UserRepository;
import com.militaryservices.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {

        User user = userRepository.findById(username)
                .orElseThrow(() -> new UsernameNotFoundException("Not found"));

        List<SimpleGrantedAuthority> authorities =
                user.getAuthorities().stream()
                        .map(auth -> new SimpleGrantedAuthority(auth.getAuthority()))
                        .toList();

        return org.springframework.security.core.userdetails.User
                .builder()
                .username(user.getUserId())
                .password(user.getPassword())
                .authorities(authorities)
                .build();
    }
}

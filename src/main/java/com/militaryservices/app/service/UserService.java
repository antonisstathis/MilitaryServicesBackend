package com.militaryservices.app.service;

import com.militaryservices.app.dao.AuthorityRepository;
import com.militaryservices.app.dao.UserRepository;
import com.militaryservices.app.dto.SignupRequest;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.entity.Authority;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.enums.MessageKey;
import com.militaryservices.app.enums.Role;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    private MessageService messageService;
    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto findUser(String username) {
        Optional<User> optionalUser =  userRepository.findById(username);
        if(optionalUser.isEmpty())
            return null;

        User user = optionalUser.get();
        return new UserDto(user.getUserId(), user.getPassword(), user.getSoldier().getId(), user.isEnabled(), user.getAuthorities());
    }

    public ResponseEntity<?> insertNewUser(SignupRequest signupRequest) {
        if (userRepository.existsById(signupRequest.getUsername()))
            return ResponseEntity.badRequest().body(messageService.getMessage(MessageKey.USER_ALREADY_EXISTS.key(), Locale.ENGLISH));

        User user = new User();
        user.setUserId(signupRequest.getUsername());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setEnabled(true);

        List<Authority> authorities = new ArrayList<>();
        authorities.add(new Authority(user, Role.SOLDIER.toString().toLowerCase()));
        //authorities.add(new Authority(user, Role.COMMANDER.toString().toLowerCase()));
        /*
        The signup endpoint is designed for a specific purpose. However, if someone tests it in its current state,
        they may notice that a user created through this endpoint will not be able to log in to the app or access
        any unauthorized data. This is because the app requires valid soldier_id and unit_id foreign keys. If
        these values are not set, or if no corresponding entry exists in the unit table, the user will not be able
        to log in regardless of their assigned role. Additionally, the app verifies user permissions on each request
        to ensure they only access data they are authorized to view.
         */

        /*
        I would like to note that an mTLS certificate must be verified before this method is invoked, and Spring Boot
        provides built-in support for that. In addition, if we aim for military-grade PKI security, a certain level of
        human, physical, and offline identity verification is required. There is no fully automated or remote-only process
        that can safely issue identity-bound certificates in a military environment.
         */
        user.setAuthorities(authorities);

        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageService.getMessage(MessageKey.NEW_USER_SAVED.key(), Locale.ENGLISH));
    }

}

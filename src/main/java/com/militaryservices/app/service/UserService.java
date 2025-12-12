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

    public ResponseEntity<?> insertNewUser(String userData, String verify,SignupRequest signupRequest) {
        if(!"SUCCESS".equals(verify))
            return ResponseEntity.badRequest().body(messageService.getMessage(MessageKey.VERIFY_CRT.key(), Locale.ENGLISH));

        CertificateDnParser.ParsedCertData parsedCertData = CertificateDnParser.parse(userData);
        if (userRepository.existsById(parsedCertData.getUsername()))
            return ResponseEntity.badRequest().body(messageService.getMessage(MessageKey.USER_ALREADY_EXISTS.key(), Locale.ENGLISH));

        User user = new User();
        user.setUserId(parsedCertData.getUsername());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setEnabled(true);

        List<Authority> authorities = new ArrayList<>();
        switch (parsedCertData.getAuthority()) {
            case "soldier":
                authorities.add(new Authority(user, Role.SOLDIER.toString().toLowerCase()));
                user.setAuthorities(authorities);
                break;

            case "commander":
                authorities.add(new Authority(user, Role.SOLDIER.toString().toLowerCase()));
                authorities.add(new Authority(user, Role.COMMANDER.toString().toLowerCase()));
                user.setAuthorities(authorities);
                break;

            default:
                throw new IllegalArgumentException("Unknown authority: " + parsedCertData.getAuthority());
        }
        user.setAuthorities(authorities);

        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageService.getMessage(MessageKey.NEW_USER_SAVED.key(), Locale.ENGLISH));
    }

}

package com.militaryservices.app.service;

import com.militaryservices.app.dao.UserRepository;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {

    private UserRepository userRepository;

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

}

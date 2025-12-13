package com.militaryservices.app.service;

import com.militaryservices.app.dao.AuthorityRepository;
import com.militaryservices.app.dao.SoldierRepository;
import com.militaryservices.app.dao.UnitRepository;
import com.militaryservices.app.dao.UserRepository;
import com.militaryservices.app.dto.SignupRequest;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.entity.Authority;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.enums.Active;
import com.militaryservices.app.enums.MessageKey;
import com.militaryservices.app.enums.Role;
import com.militaryservices.app.security.SanitizationUtil;
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

    @Autowired
    private SoldierRepository soldierRepository;
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UnitRepository unitRepository;

    @Autowired
    private MessageService messageService;
    @Autowired
    AuthorityRepository authorityRepository;

    @Autowired
    BCryptPasswordEncoder encoder;

    public UserService() {
    }

    public UserDto findUser(String username) {
        Optional<User> optionalUser =  userRepository.findById(username);
        if(optionalUser.isEmpty())
            return null;

        User user = optionalUser.get();
        return new UserDto(user.getUserId(), user.getPassword(), user.getSoldier().getId(), user.isEnabled(), user.getAuthorities());
    }

    public ResponseEntity<?> insertNewUser(String userData, String verify,SignupRequest signupRequest) {
        sanitize(signupRequest);
        if(!"SUCCESS".equals(verify))
            return ResponseEntity.badRequest().body(messageService.getMessage(MessageKey.VERIFY_CRT.key(), Locale.ENGLISH));

        CertificateDnParser.ParsedCertData parsedCertData = CertificateDnParser.parse(userData);
        if (userRepository.existsById(parsedCertData.getUsername()))
            return ResponseEntity.badRequest().body(messageService.getMessage(MessageKey.USER_ALREADY_EXISTS.key(), Locale.ENGLISH));

        User user = new User();
        user.setUserId(parsedCertData.getUsername());
        user.setPassword(encoder.encode(signupRequest.getPassword()));
        user.setEnabled(true);

        addAuthorities(user,parsedCertData);
        Unit unit = findUnit(parsedCertData.getUnitName());

        Soldier soldier = new Soldier(signupRequest.getRegistrationNumber(), signupRequest.getName(), signupRequest.getSurname(),
                signupRequest.getSituation(), Active.ACTIVE.name().toLowerCase(), true, false);
        soldier.setUnit(unit);
        user.setSoldier(soldier);
        soldierRepository.save(soldier);
        userRepository.save(user);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(messageService.getMessage(MessageKey.NEW_USER_SAVED.key(), Locale.ENGLISH));
    }

    private void addAuthorities(User user ,CertificateDnParser.ParsedCertData parsedCertData) {
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
    }

    private Unit findUnit (String unitName) {
        Unit unit;
        if (!unitRepository.existsByNameOfUnit(unitName)) {
            unit = new Unit();
            unit.setNameOfUnit(unitName);
            unitRepository.save(unit);
        }
        else
            unit = unitRepository.findByNameOfUnit(unitName).get();

        return unit;
    }

    private void sanitize(SignupRequest request) {

        request.setName(SanitizationUtil.sanitize(request.getName()));
        request.setSurname(SanitizationUtil.sanitize(request.getSurname()));
        request.setPatronymic(SanitizationUtil.sanitize(request.getPatronymic()));
        request.setMatronymic(SanitizationUtil.sanitize(request.getMatronymic()));
        request.setRegistrationNumber(SanitizationUtil.sanitize(request.getRegistrationNumber()));
        request.setTelephone(SanitizationUtil.sanitize(request.getTelephone()));
        request.setCity(SanitizationUtil.sanitize(request.getCity()));
        request.setAddress(SanitizationUtil.sanitize(request.getAddress()));
        request.setSituation(SanitizationUtil.sanitize(request.getSituation()));
        request.setPersonnelType(SanitizationUtil.sanitize(request.getPersonnelType()));
    }

}

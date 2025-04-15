package com.militaryservices.app.controller;

import com.militaryservices.app.security.JwtUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.*;

@RestController
public class TitlesController {

    @Autowired
    private MessageSource messageSource;
    @Value("${spring.messages.basename:messages}")
    private String baseName;
    @Autowired
    private JwtUtil jwtUtil;

    @GetMapping("/mtable")
    public ResponseEntity<?> getTableTitles(HttpServletRequest request,Locale locale, @RequestParam("prefix") String prefix, @RequestParam("lang") String lang) {
        if (jwtUtil.validateRequest(request)) {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
            List<String> prevCalcKeys = Arrays.asList("title.prevcalc.registration", "title.prevcalc.name", "title.prevcalc.surname", "title.prevcalc.situation",
                    "title.prevcalc.active", "title.prevcalc.service", "title.prevcalc.date", "title.prevcalc.armed", "title.prevcalc.fired");
            List<String> lastCalcKeys = Arrays.asList("title.lastcalc.name", "title.lastcalc.surname", "title.lastcalc.situation", "title.lastcalc.active",
                    "title.lastcalc.service", "title.lastcalc.date", "title.lastcalc.armed");
            List<String> orderedKeys = prefix.equals("title.prevcalc") ? prevCalcKeys : lastCalcKeys;

            List<String> result = new ArrayList<>();
            for (String key : orderedKeys) {
                key = String.join(".", key, lang);
                if (bundle.containsKey(key)) {
                    result.add(bundle.getString(key));
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

    @GetMapping("/titles")
    public ResponseEntity<?> getMainPageTitles(HttpServletRequest request,Locale locale, @RequestParam("lang") String lang) {
        if (jwtUtil.validateRequest(request)) {
            ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
            List<String> orderedKeys = Arrays.asList("title.homelabel","title.personnellabel","title.soldierslabel","title.serviceslabel",
                    "title.newservicesbutton", "title.servicesofunitbutton", "title.lastservicesbutton", "title.logoutbutton");

            Map<String,String> result = new HashMap<>();
            String value;
            for (String key : orderedKeys) {
                key = String.join(".", key, lang);
                if (bundle.containsKey(key)) {
                    value = bundle.getString(key);
                    key = key.substring(6,key.length()-3);
                    result.put(key,value);
                }
            }

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(result);
        } else
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token is invalid, expired, or missing. Please authenticate again.");
    }

}

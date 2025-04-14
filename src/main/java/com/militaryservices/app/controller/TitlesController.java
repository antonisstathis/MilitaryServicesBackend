package com.militaryservices.app.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

@RestController
public class TitlesController {

    @Autowired
    private MessageSource messageSource;
    @Value("${spring.messages.basename:messages}")
    private String baseName;

    @GetMapping("/titles")
    public List<String> getTableTitles(Locale locale, @RequestParam("prefix") String prefix) {
        //Properties props = loadPropertiesFile();
        ResourceBundle bundle = ResourceBundle.getBundle(baseName, locale);
        List<String> prevCalcKeys = Arrays.asList("title.prevcalc.registration.en","title.prevcalc.name.en","title.prevcalc.surname.en","title.prevcalc.situation.en",
                "title.prevcalc.active.en","title.prevcalc.service.en","title.prevcalc.date.en","title.prevcalc.armed.en","title.prevcalc.fired.en");
        List<String> lastCalcKeys = Arrays.asList(
                "title.lastcalc.name.en","title.lastcalc.surname.en","title.lastcalc.situation.en","title.lastcalc.active.en",
                "title.lastcalc.service.en","title.lastcalc.date.en","title.lastcalc.armed.en");
        List<String> orderedKeys = prefix.equals("title.prevcalc") ? prevCalcKeys : lastCalcKeys;

        List<String> result = new ArrayList<>();
        for (String key : orderedKeys) {
            if (bundle.containsKey(key)) {
                result.add(bundle.getString(key));
            }
        }

        return result;
    }

    private Properties loadPropertiesFile() throws IOException {
        Properties props = new Properties();
        String path = "/%s.properties".formatted(baseName.replace('.', '/'));

        try (InputStream input = getClass().getResourceAsStream(path)) {
            if (input != null) {
                props.load(input);
            } else {
                throw new IOException("Properties file not found: " + path);
            }
        }
        return props;
    }

}

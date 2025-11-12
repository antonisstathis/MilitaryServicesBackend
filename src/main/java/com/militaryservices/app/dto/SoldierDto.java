package com.militaryservices.app.dto;

import com.militaryservices.app.security.SanitizationUtil;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class SoldierDto {
    private String token;

    private String company;

    private String name;
    private String surname;
    private String situation;
    private String active;
    private String service;
    private LocalDate date;
    private String armed;

    public SoldierDto(String name, String surname, String situation, String active) {
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
    }

    public SoldierDto(String token, String company, String name, String surname, String situation, String active, String service, LocalDate date, String armed) {
        this.token = token;
        this.company = SanitizationUtil.sanitize(company);
        this.name = SanitizationUtil.sanitize(name);
        this.surname = SanitizationUtil.sanitize(surname);
        this.situation = SanitizationUtil.sanitize(situation);
        this.active = SanitizationUtil.sanitize(active);
        this.service = SanitizationUtil.sanitize(service);
        this.date = date;
        this.armed = SanitizationUtil.sanitize(armed);
    }

    public String getToken() {
        return token;
    }

    public String getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getSituation() {
        return situation;
    }

    public String getActive() {
        return active;
    }

    public String getService() {
        return service;
    }

    public String getDate() {
        if (date == null) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        return date.format(formatter);
    }

    public LocalDate extractDate() {
        return date;
    }

    public String getArmed() {
        return armed;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

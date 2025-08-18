package com.militaryservices.app.dto;

import com.militaryservices.app.security.SanitizationUtil;

public class SoldierPreviousServiceDto {

    private String token;

    private String soldierRegistrationNumber;
    private String company;

    private String name;
    private String surname;
    private String situation;
    private String active;
    private String service;
    private String date;
    private String armed;

    private String discharged;

    public SoldierPreviousServiceDto() {

    }

    public SoldierPreviousServiceDto(String token, String soldierRegistrationNumber, String company,String name, String surname, String situation, String active, String service, String date, String armed, String discharged) {
        this.token = token;
        this.soldierRegistrationNumber = SanitizationUtil.sanitize(soldierRegistrationNumber);
        this.company = SanitizationUtil.sanitize(company);
        this.name = SanitizationUtil.sanitize(name);
        this.surname = SanitizationUtil.sanitize(surname);
        this.situation = SanitizationUtil.sanitize(situation);
        this.active = SanitizationUtil.sanitize(active);
        this.service = SanitizationUtil.sanitize(service);
        this.date = SanitizationUtil.sanitize(date);
        this.armed = SanitizationUtil.sanitize(armed);
        this.discharged = SanitizationUtil.sanitize(discharged);
    }

    public String getToken() {
        return token;
    }

    public String getSoldierRegistrationNumber() {
        return soldierRegistrationNumber;
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
        return date;
    }

    public String getArmed() {
        return armed;
    }

    public String getDischarged() {
        return discharged;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSoldierRegistrationNumber(String soldierRegistrationNumber) {
        this.soldierRegistrationNumber = soldierRegistrationNumber;
    }

    public void setCompany(String company) {
        this.company = company;
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

    public void setDate(String date) {
        this.date = date;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }

    public void setDischarged(String discharged) {
        this.discharged = discharged;
    }
}

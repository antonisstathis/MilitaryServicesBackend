package com.militaryservices.app.dto;

import java.util.Date;

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
        this.soldierRegistrationNumber = soldierRegistrationNumber;
        this.company = company;
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
        this.service = service;
        this.date = date;
        this.armed = armed;
        this.discharged = discharged;
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

package com.militaryservices.app.dto;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SoldierDto {
    private String token;

    private String name;
    private String surname;
    private String situation;
    private String active;
    private String service;
    private Date date;
    private String armed;

    public SoldierDto(String name, String surname, String situation, String active) {
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
    }

    public SoldierDto(String name, String surname, String situation, String active, String service, Date date,String armed) {
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
        this.service = service;
        this.date = date;
        this.armed = armed;
    }

    public String getToken() {
        return token;
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
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
        return dateFormat.format(date);
    }

    public String getArmed() {
        return armed;
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

    public void setDate(Date date) {
        this.date = date;
    }

    public void setToken(String token) {
        this.token = token;
    }
}

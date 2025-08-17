package com.militaryservices.app.dto;

public class SoldierSelectDto {

    private String token;
    private String name;
    private String surname;
    private String situation;
    private String active;
    private String group;

    public SoldierSelectDto() {

    }

    public SoldierSelectDto(String token, String name, String surname, String situation, String active, String group) {
        this.token = token;
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
        this.group = group;
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

    public String getGroup() {
        return group;
    }

    public void setToken(String token) {
        this.token = token;
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

    public void setGroup(String group) {
        this.group = group;
    }
}

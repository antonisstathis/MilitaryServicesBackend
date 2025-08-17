package com.militaryservices.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class SoldierSelectDto {

    private String token;
    @NotBlank(message = "Name is required.")
    private String name;
    @NotBlank(message = "Surname is required.")
    private String surname;
    @NotBlank(message = "Situation is required.")
    private String situation;
    @NotBlank(message = "Active is required.")
    private String active;
    @NotBlank(message = "Group is required.")
    @Pattern(regexp = "A|B|C|D|E", message = "Group must be A or B or C or D or E'")
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

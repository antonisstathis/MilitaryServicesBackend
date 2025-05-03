package com.militaryservices.app.dto;

import jakarta.persistence.Column;

public class SoldierPersonalDataDto {

    private String token;

    private String soldierRegistrationNumber;

    private String company;

    private String name;
    private String surname;
    private String situation;
    private String active;

    private String discharged;

    private String patronymic;

    private String matronymic;

    private String mobilePhone;

    private String city;

    private String address;

    public SoldierPersonalDataDto() {
    }

    public SoldierPersonalDataDto(String token, String soldierRegistrationNumber, String company, String name, String surname, String situation, String active, String discharged, String patronymic, String matronymic, String mobilePhone, String city, String address) {
        this.token = token;
        this.soldierRegistrationNumber = soldierRegistrationNumber;
        this.company = company;
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
        this.discharged = discharged;
        this.patronymic = patronymic;
        this.matronymic = matronymic;
        this.mobilePhone = mobilePhone;
        this.city = city;
        this.address = address;
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

    public String getDischarged() {
        return discharged;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public String getMatronymic() {
        return matronymic;
    }

    public String getMobilePhone() {
        return mobilePhone;
    }

    public String getCity() {
        return city;
    }

    public String getAddress() {
        return address;
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

    public void setDischarged(String discharged) {
        this.discharged = discharged;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public void setMatronymic(String matronymic) {
        this.matronymic = matronymic;
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}

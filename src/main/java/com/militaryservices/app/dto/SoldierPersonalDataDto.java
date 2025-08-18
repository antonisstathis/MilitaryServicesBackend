package com.militaryservices.app.dto;

import com.militaryservices.app.security.SanitizationUtil;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SoldierPersonalDataDto {

    private String token;

    @NotBlank(message = "Registration number is required")
    @Size(min = 3, max = 15, message = "Registration number must be between 3 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z\u0370-\u03FF0-9]+$", message = "Name must only contain alphabetic characters (a-z, A-Z).")
    private String soldierRegistrationNumber;
    @NotBlank(message = "Company is required")
    @Size(min = 1, max = 2, message = "Company must be a number.")
    @Pattern(regexp = "^[0-9]+$", message = "The mobile phone must only contain numbers.")
    private String company;
    @NotBlank(message = "Name is required")
    @Size(min = 3, max = 15, message = "Name must be between 3 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z\u0370-\u03FF]+$", message = "Name must only contain alphabetic characters (a-z, A-Z).")
    private String name;
    @NotBlank(message = "Surname is required")
    @Size(min = 3, max = 15, message = "Surname must be between 3 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z\u0370-\u03FF]+$", message = "Surname must only contain alphabetic characters (a-z, A-Z).")
    private String surname;
    @Pattern(regexp = "armed|unarmed", message = "Situation must be 'armed' or 'unarmed'")
    private String situation;
    @Pattern(regexp = "active|inactive", message = "Status must be 'active' or 'inactive'")
    private String active;
    private String group;
    @Pattern(regexp = "A|B|C|D|E", message = "Group must be A or B or C or D or E'")
    private String discharged;

    @NotBlank(message = "Patronymic is required")
    @Size(min = 3, max = 15, message = "Patronymic must be between 3 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z\u0370-\u03FF]+$", message = "Patronymic must only contain alphabetic characters (a-z, A-Z).")
    private String patronymic;

    @NotBlank(message = "Matronymic is required")
    @Size(min = 3, max = 15, message = "Matronymic must be between 3 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z\u0370-\u03FF]+$", message = "Matronymic must only contain alphabetic characters (a-z, A-Z).")
    private String matronymic;
    @NotBlank(message = "Mobile phone is required")
    @Size(min = 5, max = 15, message = "Mobile phone must be between 3 and 15 characters.")
    @Pattern(regexp = "^[0-9]+$", message = "The mobile phone must only contain numbers.")
    private String mobilePhone;
    @NotBlank(message = "City is required")
    @Size(min = 3, max = 15, message = "City must be between 3 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z\u0370-\u03FF]+$", message = "City must only contain alphabetic characters (a-z, A-Z).")
    private String city;
    @NotBlank(message = "Address is required")
    @Size(min = 3, max = 15, message = "Address must be between 3 and 15 characters.")
    @Pattern(regexp = "^[a-zA-Z\u0370-\u03FF0-9]+$", message = "The address must only contain letters (a-z, A-Z) and numbers (0-9).")
    private String address;

    private boolean isPersonnel;

    public SoldierPersonalDataDto() {
    }

    public SoldierPersonalDataDto(String token, String soldierRegistrationNumber, String company, String name, String surname, String situation, String active, String discharged,
                                  String patronymic, String matronymic, String mobilePhone, String city, String address,boolean isPersonnel,String group) {
        this.token = token;
        this.soldierRegistrationNumber = SanitizationUtil.sanitize(soldierRegistrationNumber);
        this.company = SanitizationUtil.sanitize(company);
        this.name = SanitizationUtil.sanitize(name);
        this.surname = SanitizationUtil.sanitize(surname);
        this.situation = SanitizationUtil.sanitize(situation);
        this.active = SanitizationUtil.sanitize(active);
        this.discharged = SanitizationUtil.sanitize(discharged);
        this.patronymic = SanitizationUtil.sanitize(patronymic);
        this.matronymic = SanitizationUtil.sanitize(matronymic);
        this.mobilePhone = SanitizationUtil.sanitize(mobilePhone);
        this.city = SanitizationUtil.sanitize(city);
        this.address = SanitizationUtil.sanitize(address);
        this.isPersonnel = isPersonnel;
        this.group = SanitizationUtil.sanitize(group);
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

    public boolean isPersonnel() {
        return isPersonnel;
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

    public String getGroup() {
        return group;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public void setSoldierRegistrationNumber(String soldierRegistrationNumber) {
        this.soldierRegistrationNumber = SanitizationUtil.sanitize(soldierRegistrationNumber);
    }

    public void setCompany(String company) {
        this.company = SanitizationUtil.sanitize(company);
    }

    public void setName(String name) {
        this.name = SanitizationUtil.sanitize(name);
    }

    public void setSurname(String surname) {
        this.surname = SanitizationUtil.sanitize(surname);
    }

    public void setSituation(String situation) {
        this.situation = SanitizationUtil.sanitize(situation);
    }

    public void setActive(String active) {
        this.active = SanitizationUtil.sanitize(active);
    }

    public void setPersonnel(boolean personnel) {
        isPersonnel = personnel;
    }

    public void setDischarged(String discharged) {
        this.discharged = SanitizationUtil.sanitize(discharged);
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = SanitizationUtil.sanitize(patronymic);
    }

    public void setMatronymic(String matronymic) {
        this.matronymic = SanitizationUtil.sanitize(matronymic);
    }

    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = SanitizationUtil.sanitize(mobilePhone);
    }

    public void setCity(String city) {
        this.city = SanitizationUtil.sanitize(city);
    }

    public void setAddress(String address) {
        this.address = SanitizationUtil.sanitize(address);
    }

    public void setGroup(String group) {
        this.group = SanitizationUtil.sanitize(group);
    }
}

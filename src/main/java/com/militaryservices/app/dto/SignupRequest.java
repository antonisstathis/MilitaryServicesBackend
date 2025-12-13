package com.militaryservices.app.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class SignupRequest {

    @NotBlank(message = "Name is required")
    @Size(min = 2, max = 50)
    private String name;

    @NotBlank(message = "Surname is required")
    @Size(min = 2, max = 50)
    private String surname;

    @NotBlank(message = "Patronymic is required")
    @Size(min = 2, max = 50)
    private String patronymic;

    @NotBlank(message = "Matronymic is required")
    @Size(min = 2, max = 50)
    private String matronymic;

    @NotBlank(message = "Registration number is required")
    @Size(min = 5, max = 30)
    private String registrationNumber;

    @NotBlank(message = "Telephone is required")
    @Pattern(
            regexp = "^[0-9+]{8,15}$",
            message = "Telephone must contain only digits and optional +"
    )
    private String telephone;

    @NotBlank(message = "City is required")
    @Size(min = 2, max = 50)
    private String city;

    @NotBlank(message = "Address is required")
    @Size(min = 5, max = 100)
    private String address;

    @NotBlank(message = "Situation is required")
    @Pattern(
            regexp = "^(armed|unarmed)$",
            message = "Situation must be either 'armed' or 'unarmed'"
    )
    private String situation;

    @NotBlank(message = "Personnel type is required")
    @Pattern(
            regexp = "^(personnel|soldier)$",
            message = "Personnel type must be either 'personnel' or 'soldier'"
    )
    private String personnelType;

    @NotBlank(message = "Password is required")
    @Size(min = 8, max = 64)
    private String password;

    // ==========================
    // Getters & Setters
    // ==========================

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getPatronymic() {
        return patronymic;
    }

    public void setPatronymic(String patronymic) {
        this.patronymic = patronymic;
    }

    public String getMatronymic() {
        return matronymic;
    }

    public void setMatronymic(String matronymic) {
        this.matronymic = matronymic;
    }

    public String getRegistrationNumber() {
        return registrationNumber;
    }

    public void setRegistrationNumber(String registrationNumber) {
        this.registrationNumber = registrationNumber;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getSituation() {
        return situation;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public String getPersonnelType() {
        return personnelType;
    }

    public void setPersonnelType(String personnelType) {
        this.personnelType = personnelType;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}

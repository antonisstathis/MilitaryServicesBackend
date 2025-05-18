package com.militaryservices.app.dto;

public class SoldierServiceStatDto {

    private String soldierRegNumber;
    private String company;
    private String name;
    private String surname;
    private String active;
    private String situation;
    private int numberOfServices;

    public SoldierServiceStatDto( ) {
    }

    public SoldierServiceStatDto(String soldierRegNumber, String company, String name, String surname, String active, String situation, int numberOfServices) {
        this.soldierRegNumber = soldierRegNumber;
        this.company = company;
        this.name = name;
        this.surname = surname;
        this.active = active;
        this.situation = situation;
        this.numberOfServices = numberOfServices;
    }

    public String getSoldierRegNumber() {
        return soldierRegNumber;
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

    public String getActive() {
        return active;
    }

    public String getSituation() {
        return situation;
    }

    public int getNumberOfServices() {
        return numberOfServices;
    }

    public void setSoldierRegNumber(String soldierRegNumber) {
        this.soldierRegNumber = soldierRegNumber;
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

    public void setActive(String active) {
        this.active = active;
    }

    public void setSituation(String situation) {
        this.situation = situation;
    }

    public void setNumberOfServices(int numberOfServices) {
        this.numberOfServices = numberOfServices;
    }
}

package com.militaryservices.app.dto;

import com.militaryservices.app.entity.Unit;

public class SoldierUnitDto {

    private int id;
    private String name;
    private String surname;
    private String situation;
    private String active;
    private Unit unit;

    public SoldierUnitDto(int id, String name, String surname, String situation, String active, Unit unit) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
        this.unit = unit;
    }

    public int getId() {
        return id;
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

    public Unit getUnit() {
        return unit;
    }

    public void setId(int id) {
        this.id = id;
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

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}

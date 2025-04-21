package com.militaryservices.app.dto;

import com.militaryservices.app.entity.Unit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SoldierServiceDto {
    private int id;

    private String soldierRegistrationNumber;

    private String name;
    private String surname;
    private String situation;
    private String active;
    private long serviceId;
    private String service;
    private Date date;
    private Unit unit;
    private String armed;
    private boolean discharged;

    public SoldierServiceDto(int id,String soldierRegistrationNumber, String name, String surname, String situation, String active,long serviceId, String service, Date date,String armed,Unit unit,boolean discharged) {
        this.id = id;
        this.soldierRegistrationNumber = soldierRegistrationNumber;
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
        this.serviceId = serviceId;
        this.service = service;
        this.date = date;
        this.armed = armed;
        this.unit = unit;
        this.discharged = discharged;
    }

    public int getId() {
        return id;
    }

    public String getSoldierRegistrationNumber() {
        return soldierRegistrationNumber;
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

    public long getServiceId() {
        return serviceId;
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

    public Unit getUnit() {
        return unit;
    }

    public boolean isDischarged() {
        return discharged;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setSoldierRegistrationNumber(String soldierRegistrationNumber) {
        this.soldierRegistrationNumber = soldierRegistrationNumber;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public void setServiceId(long serviceId) {
        this.serviceId = serviceId;
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

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setDischarged(boolean discharged) {
        this.discharged = discharged;
    }
}

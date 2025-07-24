package com.militaryservices.app.dto;

import com.militaryservices.app.entity.Unit;

import java.text.SimpleDateFormat;
import java.util.Date;

public class SoldierServiceDto {
    private int id;

    private String soldierRegistrationNumber;

    private String company;

    private String name;
    private String surname;
    private String situation;
    private String active;
    private boolean isPersonnel;
    private long serviceId;
    private String service;
    private Date date;
    private Unit unit;
    private String armed;
    private boolean discharged;

    private String description;

    private String shift;

    public SoldierServiceDto(int id,String company,String soldierRegistrationNumber, String name, String surname, String situation, String active,long serviceId, String service, Date date,String armed,Unit unit,boolean discharged) {
        this.id = id;
        this.company = company;
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

    public SoldierServiceDto(int id,String company,String soldierRegistrationNumber, String name, String surname, String situation, String active,boolean isPersonnel,long serviceId, String service, Date date,String armed,Unit unit,boolean discharged,String description,String shift) {
        this.id = id;
        this.company = company;
        this.soldierRegistrationNumber = soldierRegistrationNumber;
        this.name = name;
        this.surname = surname;
        this.situation = situation;
        this.active = active;
        this.isPersonnel = isPersonnel;
        this.serviceId = serviceId;
        this.service = service;
        this.date = date;
        this.armed = armed;
        this.unit = unit;
        this.discharged = discharged;
        this.description = description;
        this.shift = shift;
    }

    public int getId() {
        return id;
    }

    public String getCompany() {
        return company;
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

    public boolean isPersonnel() {
        return isPersonnel;
    }

    public long getServiceId() {
        return serviceId;
    }

    public String getService() {
        return service;
    }

    public String getDescription() {
        return description;
    }

    public String getShift() {
        return shift;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShift(String shift) {
        this.shift = shift;
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

    public void setCompany(String company) {
        this.company = company;
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

    public void setPersonnel(boolean personnel) {
        isPersonnel = personnel;
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

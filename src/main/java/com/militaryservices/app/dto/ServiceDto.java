package com.militaryservices.app.dto;

import java.util.Date;

public class ServiceDto {

    private long id;

    private String service;
    private Date serviceDate;

    private String armed;
    private String description;
    private String shift;

    public ServiceDto() {
    }

    public ServiceDto(long id, String service, Date serviceDate, String armed, String description, String shift) {
        this.id = id;
        this.service = service;
        this.serviceDate = serviceDate;
        this.armed = armed;
        this.description = description;
        this.shift = shift;
    }

    public long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public Date getServiceDate() {
        return serviceDate;
    }

    public String getArmed() {
        return armed;
    }

    public String getDescription() {
        return description;
    }

    public String getShift() {
        return shift;
    }

    public void setId(long id) {
        this.id = id;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setServiceDate(Date serviceDate) {
        this.serviceDate = serviceDate;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }
}

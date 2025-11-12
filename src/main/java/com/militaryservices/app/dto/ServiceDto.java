package com.militaryservices.app.dto;

import com.militaryservices.app.security.SanitizationUtil;

import java.time.LocalDate;

public class ServiceDto {

    private long id;

    private String service;
    private LocalDate serviceDate;

    private String armed;
    private String description;
    private String shift;

    public ServiceDto() {
    }

    public ServiceDto(long id, String service, LocalDate serviceDate, String armed, String description, String shift) {
        this.id = id;
        this.service = SanitizationUtil.sanitize(service);
        this.serviceDate = serviceDate;
        this.armed = SanitizationUtil.sanitize(armed);
        this.description = SanitizationUtil.sanitize(description);
        this.shift = SanitizationUtil.sanitize(shift);
    }

    public long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public LocalDate getServiceDate() {
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
        this.service = SanitizationUtil.sanitize(service);
    }

    public void setServiceDate(LocalDate serviceDate) {
        this.serviceDate = serviceDate;
    }

    public void setArmed(String armed) {
        this.armed = SanitizationUtil.sanitize(armed);
    }

    public void setDescription(String description) {
        this.description = SanitizationUtil.sanitize(description);
    }

    public void setShift(String shift) {
        this.shift = SanitizationUtil.sanitize(shift);
    }
}

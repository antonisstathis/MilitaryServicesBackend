package com.militaryservices.app.dto;

public class ServiceDto {

    private Long id;

    private String service;

    private String armed;

    public ServiceDto() {
    }

    public ServiceDto(Long id, String service, String armed) {
        this.id = id;
        this.service = service;
        this.armed = armed;
    }

    public Long getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getArmed() {
        return armed;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setService(String service) {
        this.service = service;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }
}

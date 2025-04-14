package com.militaryservices.app.dto;

public class ServiceDto {

    private Long id;

    private String service;

    private String armed;
    private String description;

    public ServiceDto() {
    }

    public ServiceDto(Long id, String service, String armed,String description) {
        this.id = id;
        this.service = service;
        this.armed = armed;
        this.description = description;
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

    public String getDescription() {
        return description;
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

    public void setDescription(String description) {
        this.description = description;
    }
}

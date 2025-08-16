package com.militaryservices.app.dto;

public class ServiceOfUnitDto {

    private Long id;

    private String service;

    private String armed;
    private String description;
    private String shift;
    private String group;

    public ServiceOfUnitDto() {
    }

    public ServiceOfUnitDto(Long id, String service, String armed, String description, String shift, String group) {
        this.id = id;
        this.service = service;
        this.armed = armed;
        this.description = description;
        this.shift = shift;
        this.group = group;
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

    public String getShift() {
        return shift;
    }

    public String getGroup() {
        return group;
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

    public void setShift(String shift) {
        this.shift = shift;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

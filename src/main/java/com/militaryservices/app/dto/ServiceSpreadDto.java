package com.militaryservices.app.dto;

public class ServiceSpreadDto {

    private String serviceName;
    private Long serviceSpread;

    public ServiceSpreadDto(String serviceName, Long serviceSpread) {
        this.serviceName = serviceName;
        this.serviceSpread = serviceSpread;
    }
}

package com.militaryservices.app.dto;

import com.militaryservices.app.enums.Situation;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public class ServiceOfUnitDto {

    private Long id;

    @NotBlank(message = "Service name is required.")
    private String service;
    @NotBlank(message = "Armed is required.")
    @Pattern(regexp = "armed|unarmed", message = "Please select a valid option in the armed field.")
    private String armed;
    @NotBlank(message = "Description is required.")
    private String description;

    @NotBlank(message = "Shift is required.")
    private String shift;

    @NotBlank(message = "Group is required.")
    @Pattern(regexp = "A|B|C|D|E", message = "Please select a valid group (A, B, C, D, E).")
    private String group;

    @Min(value = 1, message = "The number of guards must be at least 1")
    @Max(value = 5, message = "The number of guards cannot exceed 5")
    private int numberOfGuards;

    public ServiceOfUnitDto() {
    }

    public ServiceOfUnitDto(String service, String armed, String description, String shift, String group) {
        this.service = service;
        this.armed = armed;
        this.description = description;
        this.shift = shift;
        this.group = group;
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

    public int getNumberOfGuards() {
        return numberOfGuards;
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

    public void setNumberOfGuards(int numberOfGuards) {
        this.numberOfGuards = numberOfGuards;
    }

    public Boolean isArmed() {

        if(Situation.ARMED.name().toLowerCase().equals(armed))
            return true;
        else
            return false;
    }
}

package com.militaryservices.app.entity;

import com.militaryservices.app.enums.Situation;
import jakarta.persistence.*;

@Entity
@Table(name = "ser_of_unit",schema = "ms")
public class ServiceOfUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ser_id")
    private Long id;

    @Column(name = "ser_name")
    private String serviceName;

    @Column
    private String armed;

    @Column
    private String company;

    @Column
    private String description;

    @Column
    private String shift;

    @Column(name = "is_personnel")
    private boolean isPersonnel;

    @Column(name = "ser_group")
    private String group;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    public ServiceOfUnit(String serviceName, String armed, String company, String description, String shift, Unit unit,boolean isPersonnel) {
        this.serviceName = serviceName;
        this.armed = armed;
        this.company = company;
        this.description = description;
        this.shift = shift;
        this.unit = unit;
        this.isPersonnel = isPersonnel;
    }

    public ServiceOfUnit(String serviceName, String armed, String company, String description, String shift, Unit unit,boolean isPersonnel,String group) {
        this.serviceName = serviceName;
        this.armed = armed;
        this.company = company;
        this.description = description;
        this.shift = shift;
        this.unit = unit;
        this.isPersonnel = isPersonnel;
        this.group = group;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescription() {
        return description;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getArmed() {
        return armed;
    }

    public String getCompany() {
        return company;
    }

    public boolean isPersonnel() {
        return isPersonnel;
    }

    public String getGroup() {
        return group;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public Boolean isArmed() {

        if(Situation.ARMED.name().toLowerCase().equals(armed))
            return true;
        else
            return false;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getShift() {
        return shift;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public ServiceOfUnit() {
    }

    public Unit getUnit() {
        return unit;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setPersonnel(boolean personnel) {
        isPersonnel = personnel;
    }

    public void setGroup(String group) {
        this.group = group;
    }
}

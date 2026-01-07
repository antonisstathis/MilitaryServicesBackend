package com.militaryservices.app.entity;

import jakarta.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "deleted_services", schema = "ms")
public class DeletedService implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ser_id")
    private Long id;

    @Column(name = "ser_name")
    private String serviceName;

    @Column
    private String armed;

    @Column(name = "ser_date")
    private LocalDate date;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    @Column
    private String description;

    @Column
    private String shift;

    @ManyToOne
    @JoinColumn(name = "sold_id", referencedColumnName = "sold_id")
    private Soldier soldier;

    @Column(name = "is_personnel")
    private boolean isPersonnel;

    @Column(name = "ser_group")
    private String group;

    @Column(name = "deleted_at", nullable = false)
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", nullable = false)
    private String deletedBy;

    public DeletedService() {
    }

    public DeletedService(Service service, String deletedBy, LocalDateTime deletedAt) {
        this.id = service.getId();
        this.serviceName = service.getServiceName();
        this.armed = service.getArmed();
        this.date = service.getDate();
        this.unit = service.getUnit();
        this.description = service.getDescription();
        this.shift = service.getShift();
        this.soldier = service.getSoldier();
        this.isPersonnel = service.isPersonnel();
        this.group = service.getGroup();
        this.deletedBy = deletedBy;
        this.deletedAt = deletedAt;
    }

    public Long getId() {
        return id;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getArmed() {
        return armed;
    }

    public LocalDate getDate() {
        return date;
    }

    public Unit getUnit() {
        return unit;
    }

    public String getDescription() {
        return description;
    }

    public String getShift() {
        return shift;
    }

    public Soldier getSoldier() {
        return soldier;
    }

    public boolean isPersonnel() {
        return isPersonnel;
    }

    public String getGroup() {
        return group;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public String getDeletedBy() {
        return deletedBy;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setShift(String shift) {
        this.shift = shift;
    }

    public void setSoldier(Soldier soldier) {
        this.soldier = soldier;
    }

    public void setPersonnel(boolean personnel) {
        isPersonnel = personnel;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public void setDeletedBy(String deletedBy) {
        this.deletedBy = deletedBy;
    }
}


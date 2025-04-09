package com.militaryservices.app.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "ser_of_unit",schema = "ms")
public class ServiceOfUnit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ser_id")
    private Long id;

    @OneToOne
    @JoinColumn(name = "ser_of_army_id")
    private ServiceOfArmy serviceOfArmy;

    @ManyToOne
    @JoinColumn(name = "unit_id")
    private Unit unit;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ServiceOfUnit() {
    }

    public ServiceOfArmy getServiceOfArmy() {
        return serviceOfArmy;
    }

    public Unit getUnit() {
        return unit;
    }

    public void setServiceOfArmy(ServiceOfArmy serviceOfArmy) {
        this.serviceOfArmy = serviceOfArmy;
    }

    public void setUnit(Unit unit) {
        this.unit = unit;
    }
}

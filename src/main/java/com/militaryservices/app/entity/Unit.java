package com.militaryservices.app.entity;

import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "unit",schema = "ms")
public class Unit {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "unit_id")
    private Long id;

    @Column(name = "name_of_unit")
    private String nameOfUnit;

    @OneToMany(mappedBy = "unit")
    private List<ServiceOfUnit> servicesOfUnit;

    @Column
    private String companies;

    public Long getId() {
        return id;
    }

    public String getNameOfUnit() {
        return nameOfUnit;
    }

    public String getCompanies() {
        return companies;
    }

    public void setCompanies(String companies) {
        this.companies = companies;
    }

    public List<ServiceOfUnit> getServicesOfUnit() {
        return servicesOfUnit;
    }
}

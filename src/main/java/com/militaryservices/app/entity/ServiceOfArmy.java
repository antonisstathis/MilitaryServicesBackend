package com.militaryservices.app.entity;

import com.militaryservices.app.dto.Situation;
import jakarta.persistence.*;

@Entity
@Table(name = "ser_of_army",schema = "ms")
public class ServiceOfArmy {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ser_id")
    private Long id;
    @Column(name = "ser_name")
    private String serviceName;

    @Column
    private String armed;

    @Column(name = "ser_description")
    private String serviceDescription;

    public ServiceOfArmy() {

    }

    public ServiceOfArmy(String serviceName, String armed) {
        this.serviceName = serviceName;
        this.armed = armed;
    }

    public String getServiceName() {
        return serviceName;
    }

    public String getArmed() {
        return armed;
    }

    public String getServiceDescription() {
        return serviceDescription;
    }

    public Boolean isArmed() {

        if(armed.equals(Situation.ARMED.name().toLowerCase()))
            return true;
        else
            return false;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public void setArmed(String armed) {
        this.armed = armed;
    }

    public void setServiceDescription(String serviceDescription) {
        this.serviceDescription = serviceDescription;
    }
}

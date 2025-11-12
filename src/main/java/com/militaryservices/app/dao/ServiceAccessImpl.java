package com.militaryservices.app.dao;

import com.militaryservices.app.entity.Service;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.enums.Active;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ServiceAccessImpl {
    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public ServiceAccessImpl(EntityManagerFactory entityManagerFactory) {

        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Transactional
    public List<Service> getServicesByDate(Unit unit, LocalDate date, boolean isPersonnel) {
        String query = "select s from Service s where s.unit =:unit and " +
                "s.date =:date and s.isPersonnel =:isPersonnel and s.armed !=:armed";
        Query nativeQuery;

        nativeQuery = entityManager.createQuery(query);
        nativeQuery.setParameter("unit",unit);
        nativeQuery.setParameter("date",date);
        nativeQuery.setParameter("isPersonnel",isPersonnel);
        nativeQuery.setParameter("armed", Active.getFreeOfDuty());
        return  nativeQuery.getResultList();
    }
}

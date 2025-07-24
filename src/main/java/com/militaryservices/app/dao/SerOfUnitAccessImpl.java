package com.militaryservices.app.dao;

import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Unit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SerOfUnitAccessImpl {

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    public SerOfUnitAccessImpl(EntityManagerFactory entityManagerFactory) {

        this.entityManager = entityManagerFactory.createEntityManager();
    }

    @Transactional
    public List<ServiceOfUnit> findByUnit(Unit unit){

        String query = "SELECT s FROM ServiceOfUnit s WHERE s.unit = :unit";

        Query nativeQuery = entityManager.createQuery(query);
        nativeQuery.setParameter("unit", unit);
        List<ServiceOfUnit> servicesOfUnit = nativeQuery.getResultList();

        return servicesOfUnit;
    }

    @Transactional
    public List<Long> countServicesOfUnit(Unit unit,boolean isPersonnel) {

        String query = "SELECT COUNT(s) FROM ServiceOfUnit s WHERE s.isPersonnel =:isPersonnel and s.unit = :unit GROUP BY s.unit";

        Query nativeQuery;
        nativeQuery = entityManager.createQuery(query);
        nativeQuery.setParameter("isPersonnel", isPersonnel);
        nativeQuery.setParameter("unit", unit);
        List<Long> countServices = nativeQuery.getResultList();

        return countServices;
    }
}

package com.militaryservices.app.service;

import com.militaryservices.app.dao.SerOfUnitRepository;
import com.militaryservices.app.dao.ServiceRepository;
import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dto.ServiceDto;
import com.militaryservices.app.enums.Situation;
import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SerOfUnitService {

    private final SerOfUnitRepository serOfUnitRepository;
    private final SoldierAccessImpl soldierAccess;
    private final ServiceRepository serviceRepository;

    public SerOfUnitService(SerOfUnitRepository repository, SoldierAccessImpl soldierAccess, ServiceRepository serviceRepository) {
        this.serOfUnitRepository = repository;
        this.soldierAccess = soldierAccess;
        this.serviceRepository = serviceRepository;
    }

    public List<ServiceDto> getAllServices(Unit unit,Date prevDate) {
        if(prevDate != null)
            return getPrevServices(unit,prevDate);

        List<ServiceOfUnit> allServices =  serOfUnitRepository.findByUnit(unit);
        List<ServiceDto> response = allServices.stream()
                .map(service -> new ServiceDto(
                        service.getId(),
                        service.getServiceName(),
                        service.getArmed(),
                        service.getDescription(),
                        service.getShift()
                ))
                .collect(Collectors.toList());

        return response;
    }

    private List<ServiceDto> getPrevServices(Unit unit,Date prevDate) {
        List<com.militaryservices.app.entity.Service> services = serviceRepository.findByUnitAndDate(unit,prevDate);
        List<ServiceDto> response = services.stream()
                .map(service -> new ServiceDto(
                        service.getId(),
                        service.getServiceName(),
                        service.getArmed(),
                        service.getDescription(),
                        service.getShift()
                ))
                .collect(Collectors.toList());
        return response;
    }

    public boolean checkIfAllowed(Unit unit,int numberOfGuards,ServiceOfUnit serviceOfUnit) {
        // Load all Soldiers
        Date dateOfLastCalculation = soldierAccess.getDateOfLastCalculation(unit);
        List<Soldier> allSoldiers = soldierAccess.loadSold(unit,dateOfLastCalculation);
        Map<Boolean, List<Soldier>> partitioned = allSoldiers.stream()
                .collect(Collectors.partitioningBy(Soldier::isArmed));
        List<Soldier> armedSoldiers = partitioned.get(true);
        List<Soldier> unarmedSoldiers = partitioned.get(false);

        // Load all Services
        List<ServiceOfUnit> armedServices = serOfUnitRepository.findByUnitAndArmed(unit, Situation.ARMED.name().toLowerCase());
        List<ServiceOfUnit> unarmedServices = serOfUnitRepository.findByUnitAndArmed(unit, Situation.UNARMED.name().toLowerCase());
        int allServices = armedServices.size() + unarmedServices.size();

        boolean canProceed = allSoldiers.size() >= (allServices + numberOfGuards)
                && (serviceOfUnit.isArmed() ? armedSoldiers.size() >= (armedServices.size() + numberOfGuards)
                : unarmedSoldiers.size() >= (unarmedServices.size() + numberOfGuards));

        return canProceed;
    }

    public ServiceOfUnit saveService(ServiceOfUnit serviceOfUnit) {
        return serOfUnitRepository.save(serviceOfUnit);
    }

    public void deleteService(Long id) {
        serOfUnitRepository.deleteById(id);
    }
}


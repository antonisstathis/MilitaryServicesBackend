package com.militaryservices.app.service;

import com.militaryservices.app.dao.SerOfUnitRepository;
import com.militaryservices.app.dao.ServiceAccessImpl;
import com.militaryservices.app.dao.ServiceRepository;
import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dto.ServiceOfUnitDto;
import com.militaryservices.app.enums.Situation;
import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SerOfUnitService {

    private final SerOfUnitRepository serOfUnitRepository;
    private final SoldierAccessImpl soldierAccess;
    private final ServiceRepository serviceRepository;
    private final ServiceAccessImpl serviceAccess;

    public SerOfUnitService(SerOfUnitRepository repository, SoldierAccessImpl soldierAccess, ServiceRepository serviceRepository,ServiceAccessImpl serviceAccess) {
        this.serOfUnitRepository = repository;
        this.soldierAccess = soldierAccess;
        this.serviceRepository = serviceRepository;
        this.serviceAccess = serviceAccess;
    }

    public List<ServiceOfUnitDto> getAllServices(Unit unit, Date prevDate,boolean isPersonnel) {

        Date dateOfLastCalculation = soldierAccess.getDateOfLastCalculation(unit, isPersonnel);
        if(prevDate != null && dateOfLastCalculation.compareTo(prevDate) != 0)
            return getPrevServices(unit,prevDate,isPersonnel);

        List<ServiceOfUnit> allServices =  serOfUnitRepository.findByUnitAndIsPersonnel(unit,isPersonnel);
        List<ServiceOfUnitDto> response = allServices.stream()
                .map(service -> new ServiceOfUnitDto(
                        service.getId(),
                        service.getServiceName(),
                        service.getArmed(),
                        service.getDescription(),
                        service.getShift(),
                        service.getGroup()
                ))
                .collect(Collectors.toList());

        return response;
    }

    private List<ServiceOfUnitDto> getPrevServices(Unit unit, Date prevDate,boolean isPersonnel) {
        List<com.militaryservices.app.entity.Service> services = serviceAccess.getServicesByDate(unit,prevDate,isPersonnel);
        List<ServiceOfUnitDto> response = services.stream()
                .map(service -> new ServiceOfUnitDto(
                        service.getId(),
                        service.getServiceName(),
                        service.getArmed(),
                        service.getDescription(),
                        service.getShift(),
                        service.getGroup()
                ))
                .collect(Collectors.toList());
        return response;
    }

    public boolean checkIfAllowed(Unit unit,int numberOfGuards,ServiceOfUnit serviceOfUnit,boolean isPersonnel) {
        // Load all Soldiers
        Date dateOfLastCalculation = soldierAccess.getDateOfLastCalculation(unit,isPersonnel);
        List<Soldier> allSoldiers = soldierAccess.loadSold(unit,dateOfLastCalculation,isPersonnel);
        Map<Boolean, List<Soldier>> partitioned = allSoldiers.stream()
                .collect(Collectors.partitioningBy(Soldier::isArmed));
        List<Soldier> armedSoldiers = partitioned.get(true);
        List<Soldier> unarmedSoldiers = partitioned.get(false);

        // Load all Services
        List<ServiceOfUnit> armedServices = serOfUnitRepository.findByUnitAndArmedAndIsPersonnel(unit, Situation.ARMED.name().toLowerCase(),isPersonnel);
        List<ServiceOfUnit> unarmedServices = serOfUnitRepository.findByUnitAndArmedAndIsPersonnel(unit, Situation.UNARMED.name().toLowerCase(),isPersonnel);
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


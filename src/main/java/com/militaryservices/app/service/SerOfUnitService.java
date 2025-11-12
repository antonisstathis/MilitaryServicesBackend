package com.militaryservices.app.service;

import com.militaryservices.app.dao.*;
import com.militaryservices.app.dto.ServiceOfUnitDto;
import com.militaryservices.app.dto.UserDto;
import com.militaryservices.app.enums.Situation;
import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.security.SanitizationUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Service
public class SerOfUnitService {

    private final SerOfUnitRepository serOfUnitRepository;
    private final SoldierAccessImpl soldierAccess;

    private final SoldierRepository soldierRepository;
    private final ServiceRepository serviceRepository;
    private final ServiceAccessImpl serviceAccess;

    public SerOfUnitService(SerOfUnitRepository repository, SoldierAccessImpl soldierAccess, ServiceRepository serviceRepository,ServiceAccessImpl serviceAccess, SoldierRepository soldierRepository) {
        this.serOfUnitRepository = repository;
        this.soldierAccess = soldierAccess;
        this.serviceRepository = serviceRepository;
        this.serviceAccess = serviceAccess;
        this.soldierRepository = soldierRepository;
    }

    public List<ServiceOfUnitDto> getAllServices(UserDto user, LocalDate prevDate, boolean isPersonnel) {

        Soldier soldier = soldierAccess.findSoldierById(user.getSoldierId());
        LocalDate dateOfLastCalculation = soldierAccess.getDateOfLastCalculation(soldier.getUnit(), isPersonnel);
        if(prevDate != null && !dateOfLastCalculation.equals(prevDate))
            return getPrevServices(soldier.getUnit(), prevDate,isPersonnel);

        List<ServiceOfUnit> allServices =  serOfUnitRepository.findByUnitAndIsPersonnel(soldier.getUnit(), isPersonnel);
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

    private List<ServiceOfUnitDto> getPrevServices(Unit unit, LocalDate prevDate,boolean isPersonnel) {
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

    public boolean checkIfAllowed(UserDto user,int numberOfGuards,ServiceOfUnitDto serviceOfUnit,boolean isPersonnel,String group) {
        Soldier soldier = soldierAccess.findSoldierById(user.getSoldierId());
        Unit unit = soldier.getUnit();
        // Load all Soldiers
        List<Soldier> allSoldiers = soldierRepository.findByUnitAndDischargedAndIsPersonnelAndGroup(unit,false,isPersonnel,group);
        Map<Boolean, List<Soldier>> partitioned = allSoldiers.stream()
                .collect(Collectors.partitioningBy(Soldier::isArmed));
        List<Soldier> armedSoldiers = partitioned.get(true);
        List<Soldier> unarmedSoldiers = partitioned.get(false);

        // Load all Services
        List<ServiceOfUnit> armedServices = serOfUnitRepository.findByUnitAndArmedAndIsPersonnelAndGroup(unit, Situation.ARMED.name().toLowerCase(),isPersonnel, group);
        List<ServiceOfUnit> unarmedServices = serOfUnitRepository.findByUnitAndArmedAndIsPersonnelAndGroup(unit, Situation.UNARMED.name().toLowerCase(),isPersonnel, group);

        int numberOfArmedServices = serviceOfUnit.isArmed() ? armedServices.size() + numberOfGuards : armedServices.size();
        int numberOfUnarmedServices = !serviceOfUnit.isArmed() ? unarmedServices.size() + numberOfGuards : unarmedServices.size();
        if(unarmedSoldiers.size() >= numberOfUnarmedServices && armedSoldiers.size() >= numberOfArmedServices)
            return true;

        if(unarmedSoldiers.size() < numberOfUnarmedServices && armedSoldiers.size() >= (numberOfArmedServices + (numberOfUnarmedServices - unarmedSoldiers.size())))
            return true;

        return false;
    }

    public void saveService(ServiceOfUnitDto serviceOfUnitDto, UserDto user, boolean isPersonnel) {

        Soldier soldier = soldierAccess.findSoldierById(user.getSoldierId());
        Unit unit = soldier.getUnit();
        IntStream.range(0, serviceOfUnitDto.getNumberOfGuards())
                .mapToObj(i -> {
                    ServiceOfUnit newService = new ServiceOfUnit(
                            SanitizationUtil.sanitize(serviceOfUnitDto.getService()),
                            SanitizationUtil.sanitize(serviceOfUnitDto.getArmed()),
                            SanitizationUtil.sanitize(serviceOfUnitDto.getDescription()),
                            SanitizationUtil.sanitize(serviceOfUnitDto.getShift()),
                            unit,
                            isPersonnel,
                            SanitizationUtil.sanitize(serviceOfUnitDto.getGroup())
                    );
                    newService.setId(null);
                    return newService;
                })
                .forEach(serOfUnitRepository::save);
    }

    public void deleteService(Long id) {
        serOfUnitRepository.deleteById(id);
    }
}


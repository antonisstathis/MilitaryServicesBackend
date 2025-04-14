package com.militaryservices.app.service;

import com.militaryservices.app.dao.SerOfUnitRepository;
import com.militaryservices.app.dto.ServiceDto;
import com.militaryservices.app.entity.ServiceOfUnit;
import com.militaryservices.app.entity.Unit;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SerOfUnitService {

    private final SerOfUnitRepository repository;

    public SerOfUnitService(SerOfUnitRepository repository) {
        this.repository = repository;
    }

    public List<ServiceDto> getAllServices(Unit unit) {
        List<ServiceOfUnit> allServices =  repository.findByUnit(unit);

        List<ServiceDto> response = new ArrayList<>();
        ServiceDto serviceDto;
        for(ServiceOfUnit service : allServices) {
            String serName = service.getServiceOfArmy().getServiceName();
            String armed = service.getServiceOfArmy().getArmed();
            serviceDto = new ServiceDto(service.getId(),serName,armed, service.getDescription());
            response.add(serviceDto);
        }

        return response;
    }

    public ServiceOfUnit saveService(ServiceOfUnit entity) {
        return repository.save(entity);
    }

    public void deleteService(Long id) {
        repository.deleteById(id);
    }
}


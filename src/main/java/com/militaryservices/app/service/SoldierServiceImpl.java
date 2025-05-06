package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dao.*;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.enums.Discharged;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.test.CheckOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class SoldierServiceImpl implements SoldierService {

	@Autowired
	private SoldierAccessImpl soldierAccess;

	@Autowired
	private CalculateServices service;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private SoldierRepository soldierRepository;
	@Autowired
	private ServiceRepository serviceRepository;
	@Autowired
	private SerOfUnitRepository serOfUnitRepository;
	@Autowired
	CheckOutput checkOutput;

	public SoldierServiceImpl() {
	}
	
	@Override
	public List<SoldierDto> findAll(String username) {

		Optional<User> user = userRepository.findById(username);
		List<Soldier> soldiers =  soldierAccess.findAll(user.get().getSoldier());
		SoldierDto soldierDto;
		List<SoldierDto> response = soldiers.stream()
				.map(sold -> {
					String token = jwtUtil.generateToken(Integer.toString(sold.getId()));
					String company = sold.getCompany();
					String name = sold.getName();
					String surname = sold.getSurname();
					String situation = sold.getSituation();
					String active = sold.getActive();
					String service = sold.getService().getServiceName();
					Date date = sold.getService().getDate();
					String armed = sold.getService().getArmed();
					return new SoldierDto(token, company, name, surname, situation, active, service, date, armed);
				})
				.collect(Collectors.toList());

		return response;
	}

	@Override
	public List<SoldierPersonalDataDto> loadSoldiers(String username) {
		Optional<User> user = userRepository.findById(username);
		Unit unit = user.get().getSoldier().getUnit();
		List<Soldier> allSoldiers = soldierRepository.findByUnitAndDischarged(unit,false);

		List<SoldierPersonalDataDto> soldierDataList = allSoldiers.stream()
				.map(soldier -> {
					SoldierPersonalDataDto soldierDto = new SoldierPersonalDataDto();
					soldierDto.setToken(jwtUtil.generateToken(Integer.toString(soldier.getId())));
					soldierDto.setSoldierRegistrationNumber(soldier.getSoldierRegistrationNumber());
					soldierDto.setCompany(soldier.getCompany());
					soldierDto.setName(soldier.getName());
					soldierDto.setSurname(soldier.getSurname());
					soldierDto.setActive(soldier.getActive());
					soldierDto.setSituation(soldier.getSituation());
					soldierDto.setDischarged(Discharged.getDischarged(soldier.isDischarged()));
					soldierDto.setPatronymic(soldier.getPatronymic());
					soldierDto.setMatronymic(soldier.getMatronymic());
					soldierDto.setMobilePhone(soldier.getMobilePhone());
					soldierDto.setCity(soldier.getCity());
					soldierDto.setAddress(soldier.getAddress());
					return soldierDto;
				})
				.collect(Collectors.toList());

		return soldierDataList;
	}

	@Override
	public List<SoldierPersonalDataDto> findSoldiersByRegistrationNumber(String registrationNumber) {
		List<Soldier> result = soldierRepository.findBySoldierRegistrationNumberContainingIgnoreCase(registrationNumber);

		List<SoldierPersonalDataDto> allSoldiers = result.stream()
				.map(soldier -> {
					SoldierPersonalDataDto dto = new SoldierPersonalDataDto();
					dto.setToken(jwtUtil.generateToken(Integer.toString(soldier.getId())));
					dto.setSoldierRegistrationNumber(soldier.getSoldierRegistrationNumber());
					dto.setCompany(soldier.getCompany());
					dto.setName(soldier.getName());
					dto.setSurname(soldier.getSurname());
					dto.setActive(soldier.getActive());
					dto.setSituation(soldier.getSituation());
					dto.setDischarged(Discharged.getDischarged(soldier.isDischarged()));
					dto.setPatronymic(soldier.getPatronymic());
					dto.setMatronymic(soldier.getMatronymic());
					dto.setMobilePhone(soldier.getMobilePhone());
					dto.setCity(soldier.getCity());
					dto.setAddress(soldier.getAddress());
					return dto;
				})
				.collect(Collectors.toList());

		return allSoldiers;
	}

	@Override
	public List<SoldierPreviousServiceDto> findPreviousCalculation(String username,Date date) {
		Optional<User> user = userRepository.findById(username);
		List<SoldierServiceDto> soldierPreviousServiceDtoList = soldierAccess.findCalculationByDate(user.get().getSoldier().getUnit(), date);

		List<SoldierPreviousServiceDto> resultList = soldierPreviousServiceDtoList.stream()
				.map(soldDto -> {
					SoldierPreviousServiceDto soldier = new SoldierPreviousServiceDto();
					soldier.setToken(jwtUtil.generateToken(Integer.toString(soldDto.getId())));
					soldier.setSoldierRegistrationNumber(soldDto.getSoldierRegistrationNumber());
					soldier.setCompany(soldDto.getCompany());
					soldier.setName(soldDto.getName());
					soldier.setSurname(soldDto.getSurname());
					soldier.setActive(soldDto.getActive());
					soldier.setSituation(soldDto.getSituation());
					soldier.setDischarged(Discharged.getDischarged(soldDto.isDischarged()));
					soldier.setService(soldDto.getService());
					soldier.setDate(soldDto.getDate());
					soldier.setArmed(soldDto.getArmed());
					return soldier;
				})
				.collect(Collectors.toList());

		return resultList;
	}

	@Override
	public Date getDateByCalculationNumber(String username,int calculation) {

		Optional<User> user = userRepository.findById(username);
		Unit unit = user.get().getSoldier().getUnit();
		return soldierAccess.getDateOfCalculation(unit,calculation);
	}

	@Override
	public void saveNewSoldier(SoldierPersonalDataDto soldierDto,Unit unit) {
		Soldier soldier = new Soldier();
		soldier.setCompany(soldierDto.getCompany());
		soldier.setSoldierRegistrationNumber(soldierDto.getSoldierRegistrationNumber());
		soldier.setName(soldierDto.getName());
		soldier.setSurname(soldierDto.getSurname());
		soldier.setActive(soldierDto.getActive());
		soldier.setSituation(soldierDto.getSituation());
		soldier.setAddress(soldierDto.getAddress());
		soldier.setCity(soldierDto.getCity());
		soldier.setUnit(unit);
		soldier.setPatronymic(soldierDto.getPatronymic());
		soldier.setMatronymic(soldierDto.getMatronymic());
		soldier.setMobilePhone(soldierDto.getMobilePhone());
		soldier.setDischarged(false);
		soldierRepository.save(soldier);
	}

	@Override
	public void deleteById(Soldier soldier) {
		/*
		try {
			store.deleteSoldier(soldier);
		} catch (IOException e) {
			e.printStackTrace();
		}
		 */
	}

	@Override
	public void calculateServices(String username) {
		
		try {
			List<Soldier> allSoldiers = service.calculateServices(username);
			service.saveNewServices(allSoldiers);
			boolean results = checkOutput.checkResults(username);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void updateSoldier(SoldDto soldier) {
		Soldier sold = soldierAccess.findSoldierById(soldier.getId());
		sold.setActive(soldier.getActive());
		sold.setSituation(soldier.getSituation());

		soldierAccess.updateSoldier(sold);
	}

	@Override
	public SoldierUnitDto findSoldier(int soldId) {
		Soldier soldier = soldierAccess.findSoldierById(soldId);
		SoldierUnitDto sold = new SoldierUnitDto(soldier.getId(), soldier.getName(), soldier.getSurname(), soldier.getSituation(),
				soldier.getActive(), soldier.getUnit());

		return sold;
	}

	@Override
	public List<ServiceDto> findServicesOfSoldier(Unit unit, int soldierId) {

		List<com.militaryservices.app.entity.Service> result = serviceRepository.findBySoldier(new Soldier(soldierId));

		List<ServiceDto> servicesOfSoldier = new ArrayList<>();
		ServiceDto serviceDto;
		for(com.militaryservices.app.entity.Service service : result) {
			serviceDto = new ServiceDto();
			serviceDto.setId(service.getId());
			serviceDto.setService(service.getServiceName());
			serviceDto.setServiceDate(service.getDate());
			serviceDto.setArmed(service.getArmed());
			serviceDto.setDescription(service.getDescription());
			serviceDto.setShift(service.getShift());
			servicesOfSoldier.add(serviceDto);
		}

		return servicesOfSoldier;
	}

	@Override
	public boolean dischargeSoldier(int soldierId,Unit unit) {
		Soldier soldier = soldierAccess.findSoldierById(soldierId);
		if(soldier.getUnit().getId() != unit.getId()) // An extra check if the user has the permission to discharge the selected soldier.
			return false;
		soldierRepository.updateDischargedStatusById(soldierId,true);
		return true;
	}

	@Override
	public void deleteServices(JsonNode services) {
		List<Long> ids = new ArrayList<>();
		services.forEach(node -> ids.add(node.asLong()));
		serOfUnitRepository.deleteAllById(ids);
	}

}

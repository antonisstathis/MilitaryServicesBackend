package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dao.SerOfUnitRepository;
import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dao.SoldierRepository;
import com.militaryservices.app.dao.UserRepository;
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
	private SerOfUnitRepository serOfUnitRepository;
	@Autowired
	CheckOutput checkOutput;

	public SoldierServiceImpl() {
	}
	
	@Override
	public List<SoldierDto> findAll(String username) {

		Optional<User> user = userRepository.findById(username);
		List<Soldier> soldiers =  soldierAccess.findAll(user.get().getSoldier());
		List<SoldierDto> response = new ArrayList<>();
		SoldierDto soldierDto;
		for(Soldier sold : soldiers) {
			String token = jwtUtil.generateToken(Integer.toString(sold.getId()));
			String company = sold.getCompany();
			String name = sold.getName();
			String surname = sold.getSurname();
			String situation = sold.getSituation();
			String active = sold.getActive();
			String service = sold.getService().getServiceName();
			Date date = sold.getService().getDate();
			String armed = sold.getService().getArmed();
			soldierDto = new SoldierDto(token,company,name,surname,situation,active,service,date,armed);
			response.add(soldierDto);
		}

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
					return soldierDto;
				})
				.collect(Collectors.toList());

		return soldierDataList;
	}

	@Override
	public List<SoldierPreviousServiceDto> findPreviousCalculation(String username,Date date) {
		Optional<User> user = userRepository.findById(username);
		List<SoldierServiceDto> soldierPreviousServiceDtoList = soldierAccess.findCalculationByDate(user.get().getSoldier().getUnit(), date);
		List<SoldierPreviousServiceDto> resultList = new ArrayList<>();

		SoldierPreviousServiceDto soldier;
		for(SoldierServiceDto soldDto : soldierPreviousServiceDtoList) {
			soldier = new SoldierPreviousServiceDto();
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
			resultList.add(soldier);
		}

		return resultList;
	}

	@Override
	public Date getDateByCalculationNumber(String username,int calculation) {

		Optional<User> user = userRepository.findById(username);
		Unit unit = user.get().getSoldier().getUnit();
		return soldierAccess.getDateOfCalculation(unit,calculation);
	}

	/*
	@Override
	public void save(Soldier soldier) {

		Optional<Option> situation = optionRepository.findByTableNameAndColumnNameAndOption("soldiers","situation", soldier.getSituation());
		Optional<Option> active = optionRepository.findByTableNameAndColumnNameAndOption("soldiers","active", soldier.getActive());
		soldier.setActive(active.get().getValue());
		soldier.setSituation(situation.get().getValue());

		if(soldier.getID() == -1) {
			try {
				int id = store.produceID();
				soldier.setID(id);
				store.saveSoldier(soldier);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		else {
			store.updateSoldier(soldier);
		}
	}
	*/

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
	public void deleteServices(JsonNode services) {
		List<Long> ids = new ArrayList<>();
		services.forEach(node -> ids.add(node.asLong()));
		serOfUnitRepository.deleteAllById(ids);
	}

}

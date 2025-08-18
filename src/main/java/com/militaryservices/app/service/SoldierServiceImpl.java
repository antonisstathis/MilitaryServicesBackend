package com.militaryservices.app.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.militaryservices.app.dao.*;
import com.militaryservices.app.dto.*;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.enums.Active;
import com.militaryservices.app.enums.Discharged;
import com.militaryservices.app.enums.Situation;
import com.militaryservices.app.enums.StatisticalData;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.test.CheckOutput;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.criteria.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
	@PersistenceContext
	private EntityManager entityManager;
	private static final Logger logger = LoggerFactory.getLogger(SoldierServiceImpl.class);
	@Autowired
	CheckOutput checkOutput;

	public SoldierServiceImpl() {
	}
	
	@Override
	public List<SoldierDto> findAll(String username,boolean isPersonnel) {

		Optional<User> user = userRepository.findById(username);
		List<Soldier> soldiers =  soldierAccess.findAll(user.get().getSoldier(),isPersonnel);
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
	public List<SoldierPersonalDataDto> loadSoldiers(String username,boolean isPersonnel) {
		Optional<User> user = userRepository.findById(username);
		Unit unit = user.get().getSoldier().getUnit();
		List<Soldier> allSoldiers = soldierRepository.findByUnitAndDischargedAndIsPersonnel(unit,false,isPersonnel);

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
					soldierDto.setGroup(soldier.getGroup());
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
	public List<SoldierPreviousServiceDto> findPreviousCalculation(String username,Date date,boolean isPersonnel) {
		Optional<User> user = userRepository.findById(username);
		List<SoldierServiceDto> soldierPreviousServiceDtoList = soldierAccess.findCalculationByDate(user.get().getSoldier().getUnit(), date, isPersonnel);

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
	public Date getDateOfLastCalculation(UserDto user, boolean isPersonnel) {
		Soldier soldier = soldierAccess.findSoldierById(user.getSoldierId());
		Date dateOfLastCalculationCalc = soldierAccess.getDateOfLastCalculation(soldier.getUnit(), isPersonnel);

		return dateOfLastCalculationCalc;
	}

	@Override
	public void saveNewSoldier(SoldierPersonalDataDto soldierDto,UserDto user) {
		Soldier sold = soldierAccess.findSoldierById(user.getSoldierId());
		Unit unit = sold.getUnit();

		Soldier soldier = new Soldier();
		Date dateOfCalc = soldierAccess.getDateOfLastCalculation(unit,soldierDto.isPersonnel());
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
		soldier.setGroup(soldierDto.getGroup());
		com.militaryservices.app.entity.Service service = new com.militaryservices.app.entity.Service("out", Active.getFreeOfDuty(), dateOfCalc, soldier.getUnit(), Active.getFreeOfDuty(),"06:00-06:00", soldierDto.isPersonnel());
		service.setSoldier(soldier);
		soldier.setService(service);
		soldierRepository.save(soldier);
		serviceRepository.save(service);
	}

	@Override
	public void calculateServices(String username,Date lastDate,boolean isPersonnel) {
		
		try {
			Optional<User> user = userRepository.findById(username);
			Unit unit = user.get().getSoldier().getUnit();
			Date dateOfLastCalculation = soldierAccess.getDateOfLastCalculation(unit,isPersonnel);
			List<String> groups = serOfUnitRepository.findDistinctGroups(unit,isPersonnel);
			if(dateOfLastCalculation.compareTo(lastDate) == 0) {
				List<Soldier> allSoldiers = calculateServicesForAllGroups(username,isPersonnel,groups);
				service.saveNewServices(allSoldiers);
				boolean results = checkOutput.checkResults(username);
			}
		} catch (IOException e) {
			logger.error("Failed to calculate services for user {}: {}", username, e.getMessage(), e);
			throw new RuntimeException(e);
		}
	}

	private List<Soldier> calculateServicesForAllGroups(String username, boolean isPersonnel,List<String> groups) throws IOException {
		List<Soldier> allSoldiers = new ArrayList<>();
		List<Soldier> soldiers;
		for(String group : groups) {
			soldiers = service.calculateServices(username,isPersonnel,group);
			allSoldiers.addAll(soldiers);
		}

		return allSoldiers;
	}

	@Override
	public void updateSoldier(SoldierSelectDto soldier) {
		Soldier sold = soldierAccess.findSoldierById(Integer.valueOf(jwtUtil.extractUsername(soldier.getToken())));
		sold.setActive(soldier.getActive());
		sold.setSituation(soldier.getSituation());
		sold.setGroup(soldier.getGroup());

		soldierAccess.updateSoldier(sold);
	}

	@Override
	public SoldierSelectDto findSoldier(int soldId) {
		Soldier soldier = soldierAccess.findSoldierById(soldId);
		SoldierSelectDto sold = new SoldierSelectDto(jwtUtil.generateToken(Integer.toString(soldier.getId())), soldier.getName(),
				soldier.getSurname(), soldier.getSituation(), soldier.getActive(), soldier.getGroup());

		return sold;
	}

	@Override
	public SoldierUnitDto findSoldierUnit(int soldId) {
		Soldier soldier = soldierAccess.findSoldierById(soldId);
		SoldierUnitDto sold = new SoldierUnitDto(soldier.getId(), soldier.getName(), soldier.getSurname(), soldier.getSituation(),
				soldier.getActive(), soldier.getUnit());

		return sold;
	}

	@Override
	public List<ServiceDto> findServicesOfSoldier(int soldierId) {

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
	public boolean dischargeSoldier(int soldierId) {
		Soldier soldier = soldierAccess.findSoldierById(soldierId);
		if(soldier.getUnit().getId() != soldier.getUnit().getId()) // An extra check if the user has the permission to discharge the selected soldier.
			return false;
		soldierRepository.updateDischargedStatusById(soldierId,true);
		return true;
	}

	@Override
	public List<SoldierServiceStatDto> getSoldierServiceStats(UserDto user, StatisticalData caseType,boolean isPersonnel) {
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<Tuple> cq = cb.createTupleQuery();
		Root<Soldier> soldier = cq.from(Soldier.class);
		Soldier sold = soldierAccess.findSoldierById(user.getSoldierId());
		Join<Soldier, com.militaryservices.app.entity.Service> service = soldier.join("services");

		List<Predicate> predicates = new ArrayList<>();

		predicates.add(cb.isFalse(soldier.get(Discharged.getDischarged())));
		predicates.add(cb.equal(soldier.get("unit"), sold.getUnit()));
		String situation = "";
		switch (caseType) {
			case ARMED_SERVICES_ARMED_SOLDIERS:
				situation = Situation.ARMED.name().toLowerCase();
				predicates.add(cb.equal(service.get(Situation.ARMED.name().toLowerCase()), Situation.ARMED.name().toLowerCase()));
				break;

			case UNARMED_SERVICES_ARMED_SOLDIERS:
				situation = Situation.ARMED.name().toLowerCase();
				predicates.add(cb.equal(service.get(Situation.ARMED.name().toLowerCase()), Situation.UNARMED.name().toLowerCase()));
				predicates.add(cb.equal(soldier.get(Situation.getNameOfColumn()), Situation.ARMED.name().toLowerCase()));
				break;

			case UNARMED_SERVICES_UNARMED_SOLDIERS:
				situation = Situation.UNARMED.name().toLowerCase();
				predicates.add(cb.equal(service.get(Situation.ARMED.name().toLowerCase()), Situation.UNARMED.name().toLowerCase()));
				predicates.add(cb.equal(soldier.get(Situation.getNameOfColumn()), Situation.UNARMED.name().toLowerCase()));
				break;

			case FREE_OF_DUTY_SERVICES_ALL_SOLDIERS:
				predicates.add(cb.equal(service.get(Situation.ARMED.name().toLowerCase()), Active.getFreeOfDuty()));
				break;
		}
		predicates.add(cb.equal(soldier.get("isPersonnel"), isPersonnel));

		List<SoldierServiceStatDto> statDtoList = soldierAccess.getSoldierServiceStatisticalData(cq, predicates, soldier, service, cb);

		if(statDtoList.size() == 0)
			return createListInCaseOfZeroServices(sold.getUnit(), isPersonnel,situation);

		return statDtoList;
	}

	private List<SoldierServiceStatDto> createListInCaseOfZeroServices(Unit unit, boolean isPersonnel, String situation) {
		List<Soldier> statData = soldierRepository.findByUnitAndDischargedAndIsPersonnelAndSituation(unit,false,isPersonnel,situation);
		List<SoldierServiceStatDto> statDtoList = new ArrayList<>();

		SoldierServiceStatDto soldierServiceStatDto;
		for(Soldier soldier : statData) {
			soldierServiceStatDto = new SoldierServiceStatDto();
			soldierServiceStatDto.setCompany(soldier.getCompany());
			soldierServiceStatDto.setSoldierRegNumber(soldier.getSoldierRegistrationNumber());
			soldierServiceStatDto.setName(soldier.getName());
			soldierServiceStatDto.setSurname(soldier.getSurname());
			soldierServiceStatDto.setActive(soldier.getActive());
			soldierServiceStatDto.setSituation(soldier.getSituation());
			soldierServiceStatDto.setNumberOfServices(0);
			statDtoList.add(soldierServiceStatDto);
		}

		return statDtoList;
	}

	@Override
	public void deleteServices(JsonNode services) {
		List<Long> ids = new ArrayList<>();
		services.forEach(node -> ids.add(node.asLong()));
		serOfUnitRepository.deleteAllById(ids);
	}

}

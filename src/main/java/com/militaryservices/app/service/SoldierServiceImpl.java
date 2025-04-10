package com.militaryservices.app.service;

import com.militaryservices.app.dao.SoldierAccessImpl;
import com.militaryservices.app.dao.UserRepository;
import com.militaryservices.app.dto.SoldDto;
import com.militaryservices.app.dto.SoldierDto;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.User;
import com.militaryservices.app.security.JwtUtil;
import com.militaryservices.app.test.CheckOutput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

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
			String name = sold.getName();
			String surname = sold.getSurname();
			String situation = sold.getSituation();
			String active = sold.getActive();
			String service = sold.getService().getServiceName();
			Date date = sold.getService().getDate();
			String armed = sold.getService().getArmed();
			soldierDto = new SoldierDto(name,surname,situation,active,service,date,armed);
			soldierDto.setToken(jwtUtil.generateToken(Integer.toString(sold.getId())));
			response.add(soldierDto);
		}

		return response;
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

}

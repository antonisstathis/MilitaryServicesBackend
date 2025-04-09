package com.militaryservices.app.dao;

import com.militaryservices.app.dto.HistoricalData;
import com.militaryservices.app.dto.SoldierServiceDto;
import com.militaryservices.app.entity.Service;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Component
public class SoldierAccessImpl {

	@PersistenceContext
	private EntityManager entityManager;

	@Autowired
	public SoldierAccessImpl(EntityManagerFactory entityManagerFactory) {

		this.entityManager = entityManagerFactory.createEntityManager();
	}

	@Transactional
	public void saveSoldier(Soldier soldier,Date currentDate) {

		String query = "INSERT INTO Soldier (id,name,surname,situation,active,fired) VALUES (:id, :name, :surname, :situation, :active, :fired)";
		String query1 = "INSERT INTO Service (serviceName,armed,date,soldierId,calculation) VALUES (:serName, :armed, :serDate, :soldId, :calculation)";

		Query nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("name",soldier.getName());
		nativeQuery.setParameter("surname",soldier.getSurname());
		nativeQuery.setParameter("situation",soldier.getSituation());
		nativeQuery.setParameter("active",soldier.getActive());
		nativeQuery.setParameter("fired",false);
		int result = nativeQuery.executeUpdate();

		nativeQuery = entityManager.createQuery(query1);
		nativeQuery.setParameter("serName", "out");
		nativeQuery.setParameter("armed","");
		nativeQuery.setParameter("serDate",currentDate);
		nativeQuery.setParameter("soldId",soldier.getId());
		nativeQuery.setParameter("calculation",getCalculations(soldier.getUnit()));
		result = nativeQuery.executeUpdate();
	}
	@Transactional
	public void saveSoldiers(List<Soldier> allSoldiers) throws IOException, SQLException {

		int calculation = updateCalculationNumber(allSoldiers.get(0).getUnit());
		Service service;
		for(Soldier sold : allSoldiers) {
			service = sold.getService();
			service.setCalculation(calculation);
			service.setSoldier(sold);
			service.setUnit(sold.getUnit());
			entityManager.persist(sold.getService());
		}
	}

	@Transactional
	public List<Soldier> loadSold(Unit unit) {

		String query = "select distinct new com.militaryservices.app.dto.SoldierServiceDto(s.id,s.name,s.surname,s.situation,s.active,u.id,u.serviceName,u.date,u.armed,s.unit) " +
				"from Soldier s inner join Service u on (s = u.soldier) where s.unit =:unit and s.fired =:fired and u.calculation =:calculation order by s.id asc";
		Query nativeQuery;

		int calculations = 0;
		calculations = getCalculations(unit);

		List<Soldier> allSoldiers = new ArrayList<>();
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit",unit);
		nativeQuery.setParameter("fired", false);
		nativeQuery.setParameter("calculation", calculations);
		List<SoldierServiceDto> list = nativeQuery.getResultList();
		Soldier sold;
		Service service;
		for(SoldierServiceDto soldierDto : list) {
			sold = new Soldier(soldierDto.getId(),soldierDto.getName(),soldierDto.getSurname(),soldierDto.getSituation(),soldierDto.getActive());
			service = new Service(soldierDto.getService(),soldierDto.getArmed(),convertStringToDate(soldierDto.getDate()),soldierDto.getUnit());
			sold.setService(service);
			sold.setUnit(service.getUnit());
			allSoldiers.add(sold);
		}

		return allSoldiers;
	}

	/*
	@Transactional
	public void updateSoldier(Soldier soldier) {

		entityManager.merge(soldier);
		Soldier sold = findById(soldier.getID());
		sold.setName(soldier.getName());
		sold.setSurname(soldier.getSurname());
		sold.setSituation(soldier.getSituation());
		sold.setActive(soldier.getActive());
	}
	 */

	/*
	@Transactional
	public void deleteSoldier(Soldier soldier) throws IOException {

		int index = findIndexOfSoldier(soldier.getID());
		Scanner sc = new Scanner(new File("pointers.txt"));
		String ch = sc.next();
		int nextOutgoing = Integer.parseInt(String.valueOf(ch));
		if(nextOutgoing > index) {
			nextOutgoing--;
			Writer wr = new FileWriter("pointers.txt");
			wr.write(Integer.toString(nextOutgoing));
			wr.close();
		}

		//allSoldiers.remove(index);
		entityManager.merge(soldier);
	}
	*/

	@Transactional
	public List<HistoricalData> getHistoricalDataDesc(Unit unit,String armed) {

		String query = "select new com.militaryservices.app.dto.HistoricalData(s.id, count(*)) from Soldier s inner join Service u on " +
				"(s = u.soldier) where s.unit = :unit and s.fired = :fired and u.armed = :armed and u.calculation >= :calculation group by s.id order by count(*) desc";

		Query nativeQuery;
		List<HistoricalData> historicalData;
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit", unit);
		nativeQuery.setParameter("fired", false);
		nativeQuery.setParameter("armed", armed);
		nativeQuery.setParameter("calculation",1);
		historicalData = nativeQuery.getResultList();

		return historicalData;
	}

	@Transactional
	public List<HistoricalData> getHistoricalDataAsc(Unit unit,String armed) {

		String query = "select new com.militaryservices.app.dto.HistoricalData(s.id, count(*)) from Soldier s inner join Service u on " +
				"(s = u.soldier) where s.unit = :unit and s.fired =:fired and u.armed =:armed and u.calculation >= :calculation group by s.id order by count(*) asc";

		Query nativeQuery;
		List<HistoricalData> historicalData;
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("fired", false);
		nativeQuery.setParameter("armed", armed);
		nativeQuery.setParameter("calculation",1);
		historicalData = nativeQuery.getResultList();

		return historicalData;
	}

	@Transactional
	public List<HistoricalData> countServicesForTheLastNDays(Unit unit,int numberOfFirstDayCalculation) {

		String query = "select new com.militaryservices.app.dto.HistoricalData(s.id, count(*)) from Soldier s inner join Service u on " +
				"(s = u.soldier) where s.unit =:unit and s.fired =:fired and u.armed <>:armed and u.calculation >= :calculation group by s.id order by count(*) desc";

		Query nativeQuery;
		List<HistoricalData> historicalData;
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit", unit);
		nativeQuery.setParameter("fired", false);
		nativeQuery.setParameter("armed", "έξοδος");
		nativeQuery.setParameter("calculation",numberOfFirstDayCalculation);
		historicalData = nativeQuery.getResultList();

		return historicalData;
	}

	public int getCalculations(Unit unit) {

		String query = "select MAX(s.calculation) from Service s where s.unit =:unit";

		Query nativeQuery;
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit", unit);
		return (int) nativeQuery.getSingleResult();
	}

	public int updateCalculationNumber(Unit unit) {
		int id = getCalculations(unit);
		id+=1;
		return id;
	}
	
	public List<Soldier> findAll(Soldier soldier) {

		return loadSold(soldier.getUnit());
	}

	private Date convertStringToDate(String date) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		try {
			return dateFormat.parse(date);
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}
	}

}
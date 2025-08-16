package com.militaryservices.app.dao;

import com.militaryservices.app.dto.SoldierServiceStatDto;
import com.militaryservices.app.dto.HistoricalData;
import com.militaryservices.app.dto.SoldierServiceDto;
import com.militaryservices.app.entity.Service;
import com.militaryservices.app.entity.Soldier;
import com.militaryservices.app.entity.Unit;
import jakarta.persistence.*;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

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

		String query = "INSERT INTO Soldier (id,name,surname,situation,active,discharged) VALUES (:id, :name, :surname, :situation, :active, :discharged)";
		String query1 = "INSERT INTO Service (serviceName,armed,date,soldierId) VALUES (:serName, :armed, :serDate, :soldId)";

		Query nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("name",soldier.getName());
		nativeQuery.setParameter("surname",soldier.getSurname());
		nativeQuery.setParameter("situation",soldier.getSituation());
		nativeQuery.setParameter("active",soldier.getActive());
		nativeQuery.setParameter("discharged",false);
		int result = nativeQuery.executeUpdate();

		nativeQuery = entityManager.createQuery(query1);
		nativeQuery.setParameter("serName", "out");
		nativeQuery.setParameter("armed","");
		nativeQuery.setParameter("serDate",currentDate);
		nativeQuery.setParameter("soldId",soldier.getId());
		result = nativeQuery.executeUpdate();
	}
	@Transactional
	public void saveSoldiers(List<Soldier> allSoldiers) throws IOException, SQLException {

		allSoldiers.stream()
				.map(sold -> {
					Service service = sold.getService();
					service.setSoldier(sold);
					service.setUnit(sold.getUnit());
					return service;
				})
				.forEach(entityManager::persist);
	}

	@Transactional
	public List<Soldier> loadSold(Unit unit,Date dateOfLastCalc,boolean isPersonnel) {

		String query = "select distinct new com.militaryservices.app.dto.SoldierServiceDto(s.id,s.company,s.soldierRegistrationNumber,s.name,s.surname,s.situation,s.active,s.isPersonnel, u.id,u.serviceName, " +
				"u.date,u.armed,s.unit,s.discharged, u.description, u.shift) from Soldier s inner join Service u on (s = u.soldier) where s.unit =:unit and s.discharged =:discharged " +
				"and s.isPersonnel =:isPersonnel and u.date =:date order by s.id asc";
		Query nativeQuery;

		//List<Soldier> allSoldiers = new ArrayList<>();
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit",unit);
		nativeQuery.setParameter("discharged", false);
		nativeQuery.setParameter("isPersonnel", isPersonnel);
		nativeQuery.setParameter("date", dateOfLastCalc);
		List<SoldierServiceDto> list = nativeQuery.getResultList();

		List<Soldier> allSoldiers = list.stream()
				.map(soldierDto -> {
					Soldier sold = new Soldier(soldierDto.getId(), soldierDto.getCompany(), soldierDto.getSoldierRegistrationNumber(),soldierDto.getName(),soldierDto.getSurname(),soldierDto.getSituation(),soldierDto.getActive(), soldierDto.isPersonnel(), soldierDto.isDischarged());
					Service service = new Service(soldierDto.getService(),soldierDto.getArmed(),convertStringToDate(soldierDto.getDate()),soldierDto.getUnit(), soldierDto.getCompany(), soldierDto.getDescription(), soldierDto.getShift(), isPersonnel);
					sold.setService(service);
					sold.setUnit(service.getUnit());
					return sold;
				}).collect(Collectors.toList());

		return allSoldiers;
	}

	@Transactional
	public List<Soldier> loadSoldByGroup(Unit unit,Date dateOfLastCalc,boolean isPersonnel, String group) {

		String query = "select distinct new com.militaryservices.app.dto.SoldierServiceDto(s.id,s.company,s.soldierRegistrationNumber,s.name,s.surname,s.situation,s.active,s.isPersonnel,s.group,u.id,u.serviceName, " +
				"u.date,u.armed,s.unit,s.discharged, u.description, u.shift) from Soldier s inner join Service u on (s = u.soldier) where s.unit =:unit and s.discharged =:discharged " +
				"and s.isPersonnel =:isPersonnel and u.date =:date and s.group =:group order by s.id asc";
		Query nativeQuery;

		//List<Soldier> allSoldiers = new ArrayList<>();
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit",unit);
		nativeQuery.setParameter("discharged", false);
		nativeQuery.setParameter("isPersonnel", isPersonnel);
		nativeQuery.setParameter("date", dateOfLastCalc);
		nativeQuery.setParameter("group", group);
		List<SoldierServiceDto> list = nativeQuery.getResultList();

		List<Soldier> allSoldiers = list.stream()
				.map(soldierDto -> {
					Soldier sold = new Soldier(soldierDto.getId(), soldierDto.getCompany(), soldierDto.getSoldierRegistrationNumber(),soldierDto.getName(),soldierDto.getSurname(),soldierDto.getSituation(),soldierDto.getActive(), soldierDto.getGroup(), soldierDto.isPersonnel(), soldierDto.isDischarged());
					Service service = new Service(soldierDto.getService(),soldierDto.getArmed(),convertStringToDate(soldierDto.getDate()),soldierDto.getUnit(), soldierDto.getCompany(), soldierDto.getDescription(), soldierDto.getShift(), isPersonnel);
					sold.setService(service);
					sold.setUnit(service.getUnit());
					return sold;
				}).collect(Collectors.toList());

		return allSoldiers;
	}

	public Date getDateOfCalculation(Unit unit,int calculations) {

		Date dateOfFirstCalculation = getDateOfFirstCalculation(unit);
		Calendar cal = Calendar.getInstance();
		cal.setTime(dateOfFirstCalculation);
		cal.add(Calendar.DAY_OF_MONTH, calculations-1);
		return cal.getTime();
	}

	@Transactional
	public Date getDateOfFirstCalculation(Unit unit) {
		String query = "select distinct u.date from Service u where u.unit =:unit and u.date = (select min(s.date) from Service s)";
		Query nativeQuery;

		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit",unit);
		return (Date) nativeQuery.getSingleResult();
	}

	@Transactional
	public Date getDateOfLastCalculation(Unit unit,boolean isPersonnel) {
		String query = "select distinct u.date from Service u where u.isPersonnel =:isPersonnel and u.unit =:unit and u.date = (select max(s.date) from Service s " +
				"where s.isPersonnel =:isPersonnel)";
		Query nativeQuery;

		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("isPersonnel",isPersonnel);
		nativeQuery.setParameter("unit",unit);
		return (Date) nativeQuery.getSingleResult();
	}

	@Transactional
	public List<SoldierServiceDto> findCalculationByDate(Unit unit, Date date,boolean isPersonnel) {
		String query = "select distinct new com.militaryservices.app.dto.SoldierServiceDto(s.id,s.company,s.soldierRegistrationNumber,s.name,s.surname,s.situation,s.active,u.id,u.serviceName,u.date,u.armed,s.unit,s.discharged) " +
				"from Soldier s inner join Service u on (s = u.soldier) where s.unit =:unit and s.isPersonnel =:isPersonnel and u.date =:date order by s.id asc";
		Query nativeQuery;

		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit",unit);
		nativeQuery.setParameter("isPersonnel",isPersonnel);
		nativeQuery.setParameter("date",date);
		return  nativeQuery.getResultList();
	}

	@Transactional
	public void updateSoldier(Soldier soldier) {

		entityManager.merge(soldier);
	}

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
	public List<HistoricalData> getHistoricalDataDesc(Unit unit,String armed,boolean isPersonnel, String group) {

		String query = "select new com.militaryservices.app.dto.HistoricalData(s.id, count(*)) from Soldier s inner join Service u on " +
				"(s = u.soldier) where s.unit = :unit and s.isPersonnel =:isPersonnel and s.discharged = :discharged and s.group =:group " +
				"and u.armed = :armed group by s.id order by count(*) desc";

		Query nativeQuery;
		List<HistoricalData> historicalData;
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("unit", unit);
		nativeQuery.setParameter("isPersonnel", isPersonnel);
		nativeQuery.setParameter("discharged", false);
		nativeQuery.setParameter("armed", armed);
		nativeQuery.setParameter("group", group);
		historicalData = nativeQuery.getResultList();

		return historicalData;
	}

	@Transactional
	public List<HistoricalData> getHistoricalDataAsc(Unit unit,String armed) {

		String query = "select new com.militaryservices.app.dto.HistoricalData(s.id, count(*)) from Soldier s inner join Service u on " +
				"(s = u.soldier) where s.unit = :unit and s.discharged =:discharged and u.armed =:armed group by s.id order by count(*) asc";

		Query nativeQuery;
		List<HistoricalData> historicalData;
		nativeQuery = entityManager.createQuery(query);
		nativeQuery.setParameter("discharged", false);
		nativeQuery.setParameter("armed", armed);
		historicalData = nativeQuery.getResultList();

		return historicalData;
	}

	@Transactional
	public List<SoldierServiceStatDto> getSoldierServiceStatisticalData(CriteriaQuery<Tuple> cq,List<Predicate> predicates,Root<Soldier> soldier,Join<Soldier, Service> service,CriteriaBuilder cb) {

		Expression<Long> countExpr = cb.count(service);
		cq.multiselect(
						soldier.get("soldierRegistrationNumber").alias("soldierRegNumber"),
						soldier.get("company").alias("company"),
						soldier.get("name").alias("name"),
						soldier.get("surname").alias("surname"),
						soldier.get("active").alias("active"),
						soldier.get("situation").alias("situation"),
						countExpr.alias("numberOfServices")
				)
				.where(predicates.toArray(new Predicate[0]))
				.groupBy(soldier.get("id"))
				.orderBy(cb.desc(countExpr));

		List<Tuple> results = entityManager.createQuery(cq).getResultList();

		return results.stream()
				.map(t -> new SoldierServiceStatDto(
						t.get("soldierRegNumber", String.class),
						t.get("company", String.class),
						t.get("name", String.class),
						t.get("surname", String.class),
						t.get("active", String.class),
						t.get("situation", String.class),
						((Number) t.get("numberOfServices")).intValue()
				))
				.collect(Collectors.toList());
	}

	@Transactional
	public Soldier findSoldierById(int soldId) {
		return entityManager.find(Soldier.class,soldId);
	}
	
	public List<Soldier> findAll(Soldier soldier,boolean isPersonnel) {

		Date dateOfLastCalculation = getDateOfLastCalculation(soldier.getUnit(),isPersonnel);
		return loadSold(soldier.getUnit(),dateOfLastCalculation,isPersonnel);
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
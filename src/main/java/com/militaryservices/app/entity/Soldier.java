package com.militaryservices.app.entity;

import com.militaryservices.app.enums.Active;
import com.militaryservices.app.enums.Situation;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "soldiers", schema = "ms")
public class Soldier implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "sold_id")
	private int id;

	@Column
	private String soldierRegistrationNumber;

	@Column
	@NotNull(message="is required")
	@Size(min=3, max=20, message="enter at least 3 characters")
	private String name;

	@Column
	@NotNull(message="is required")
	@Size(min=3, max=20, message="enter at least 3 characters")
	private String surname;
	
	@Column
	private String situation;
	@Column
	private String active;

	@OneToMany(mappedBy = "id")
	private List<Service> services;
	@Column
	private boolean discharged;
	@Column
	private String company;

	@Column
	private String patronymic;

	@Column
	private String matronymic;

	@Column(name = "mobile_phone")
	private String mobilePhone;

	@Column
	private String city;

	@Column
	private String address;

	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unit unit;
	
	public Soldier() {
		
	}

	public Soldier(int soldId) {
		this.id = soldId;
	}

	public Soldier(int id,String name,String surname,String situation,String active,boolean discharged)  {
		
		this.name = name;
		this.surname = surname;
		this.situation = situation;
		this.active = active;
		this.id = id;
		this.discharged = discharged;
		services = new ArrayList<>();
	}

	public Soldier(int id,String company,String soldierRegistrationNumber,String name,String surname,String situation,String active,boolean discharged)  {

		this.name = name;
		this.company = company;
		this.surname = surname;
		this.situation = situation;
		this.active = active;
		this.id = id;
		this.soldierRegistrationNumber = soldierRegistrationNumber;
		this.discharged = discharged;
		services = new ArrayList<>();
	}

	public Soldier(int id, String soldierRegistrationNumber, String name, String surname, String situation, String active, List<Service> services, boolean discharged, String company, String patronymic, String matronymic, String mobilePhone, String city, String address, Unit unit) {
		this.id = id;
		this.soldierRegistrationNumber = soldierRegistrationNumber;
		this.name = name;
		this.surname = surname;
		this.situation = situation;
		this.active = active;
		this.services = services;
		this.discharged = discharged;
		this.company = company;
		this.patronymic = patronymic;
		this.matronymic = matronymic;
		this.mobilePhone = mobilePhone;
		this.city = city;
		this.address = address;
		this.unit = unit;
	}

	public String getName() {
		
		return name;
	}
	
	public String getSurname() {
		
		return surname;
	}
	
	public String getSituation() {
		
		return situation;
	}
	
	public String getActive() {
		
		return active;
	}
	
	public Service getService() {
		
		return services.get(0);
	}

	public Unit getUnit() {
		return unit;
	}

	public String getSoldierRegistrationNumber() {
		return soldierRegistrationNumber;
	}

	public List<Service> getAllServices() {
		return services;
	}

	public String getCompany() {
		return company;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setSoldierRegistrationNumber(String soldierRegistrationNumber) {
		this.soldierRegistrationNumber = soldierRegistrationNumber;
	}

	public boolean isDischarged() {
		return discharged;
	}

	public List<Service> getServices() {
		return services;
	}

	public String getPatronymic() {
		return patronymic;
	}

	public String getMatronymic() {
		return matronymic;
	}

	public String getMobilePhone() {
		return mobilePhone;
	}

	public String getCity() {
		return city;
	}

	public String getAddress() {
		return address;
	}

	public int getId() {
		
		return id;
	}
	
	public void setID(int id) {
		
		this.id = id;
	}
	
	public void setName(String name) {
		
		this.name = name;
	}
	
	public void setSurname(String surname) {
		
		this.surname = surname;
	}
	
	public void setSituation(String situation) {
		
		this.situation = situation;
	}
	
	public void setService(Service service) {

		services.clear();
		services.add(service);
	}
	
	public void setActive(String active) {
		
		this.active = active;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}

	public void setPatronymic(String patronymic) {
		this.patronymic = patronymic;
	}

	public void setMatronymic(String matronymic) {
		this.matronymic = matronymic;
	}

	public void setMobilePhone(String mobilePhone) {
		this.mobilePhone = mobilePhone;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

	public void setDischarged(boolean discharged) {
		this.discharged = discharged;
	}

	public boolean checkIfActive() {

		if(Active.ACTIVE.name().toLowerCase().equals(active))
			return true;
		else
			return false;
	}

	public boolean isArmed() {
		if(Situation.ARMED.name().toLowerCase().equals(situation))
			return true;
		else
			return false;
	}

	public boolean isOut() {
		if("out".equals(getService().getServiceName()))
			return true;
		else
			return false;
	}

	public boolean isAvailable() {
		if("available".equals(getService().getServiceName()))
			return true;
		else
			return false;
	}

	public void print() {
		
		String s = name + " " + surname + " " + situation + " " + active;

		System.out.println(s);
	}
	
}

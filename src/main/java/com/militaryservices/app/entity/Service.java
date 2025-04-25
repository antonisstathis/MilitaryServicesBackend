package com.militaryservices.app.entity;

import com.militaryservices.app.dto.Situation;
import jakarta.persistence.*;

import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "services",schema = "ms")
public class Service implements Serializable {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ser_id")
	private Long id;
	@Column(name = "ser_name")
	private String serviceName;
	@Column
	private String armed;

	@Column(name = "ser_date")
	private Date date;

	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unit unit;

	@Column
	private String company;

	@Column
	private String description;

	@Column
	private String shift;

	@ManyToOne
	@JoinColumn(name = "sold_id", referencedColumnName = "sold_id")
	private Soldier soldier;

	public Service() {

	}
	
	public Service(String serviceName,String armed,Date date,Unit unit) {
		this.serviceName = serviceName;
		this.armed = armed;
		this.date = date;
		this.unit = unit;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getArmed() {
		
		return armed;
	}

	public void setArmed(String armed) {
		this.armed = armed;
	}

	public void setSoldier(Soldier soldier) {
		this.soldier = soldier;
	}

	public void setCompany(String company) {
		this.company = company;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setShift(String shift) {
		this.shift = shift;
	}

	public Soldier getSoldier() {
		return soldier;
	}
	
	public Date getDate() {
		
		return date;
	}

	public void setDate(Date date) {
		
		this.date = date;
	}
	
	public Long getID() {
		
		return id;
	}

	public String getCompany() {
		return company;
	}

	public String getDescription() {
		return description;
	}

	public String getShift() {
		return shift;
	}

	public Boolean isArmed() {

		if(Situation.ARMED.name().toLowerCase().equals(armed))
			return true;
		else
			return false;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}
}

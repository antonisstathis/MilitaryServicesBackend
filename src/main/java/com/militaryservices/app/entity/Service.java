package com.militaryservices.app.entity;

import com.militaryservices.app.enums.Situation;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDate;

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
	private LocalDate date;

	@ManyToOne
	@JoinColumn(name = "unit_id")
	private Unit unit;

	@Column
	private String description;

	@Column
	private String shift;

	@ManyToOne
	@JoinColumn(name = "sold_id", referencedColumnName = "sold_id")
	private Soldier soldier;

	@Column(name = "is_personnel")
	private boolean isPersonnel;

	@Column(name = "ser_group")
	private String group;

	public Service() {

	}

	public Service(String serviceName,String armed,LocalDate date) {
		this.serviceName = serviceName;
		this.armed = armed;
		this.date = date;
	}
	
	public Service(String serviceName,String armed,LocalDate date,Unit unit) {
		this.serviceName = serviceName;
		this.armed = armed;
		this.date = date;
		this.unit = unit;
	}

	public Service(String serviceName, String armed, LocalDate date, Unit unit, String description, String shift,boolean isPersonnel) {
		this.serviceName = serviceName;
		this.armed = armed;
		this.date = date;
		this.unit = unit;
		this.description = description;
		this.shift = shift;
		this.isPersonnel = isPersonnel;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

	public String getGroup() {
		return group;
	}

	public void setGroup(String group) {
		this.group = group;
	}

	public String getArmed() {
		
		return armed;
	}

	public boolean isPersonnel() {
		return isPersonnel;
	}

	public void setArmed(String armed) {
		this.armed = armed;
	}

	public void setSoldier(Soldier soldier) {
		this.soldier = soldier;
	}

	public void setPersonnel(boolean personnel) {
		isPersonnel = personnel;
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
	
	public LocalDate getDate() {
		
		return date;
	}

	public void setDate(LocalDate date) {
		
		this.date = date;
	}
	
	public Long getId() {
		
		return id;
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

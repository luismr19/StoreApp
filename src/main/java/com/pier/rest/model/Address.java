package com.pier.rest.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Entity
@Table(name = "address")
public class Address implements ObjectModel<Long>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="COUNTRY",length=15)	
	@NotNull
	@Size(min=3, max=15)
	private String country;
	
	@Column(name="CITY",length=17)	
	@NotNull
	@Size(min=3, max=15)
	private String city;
	
	@Column(name="STATE",length=15)	
	@NotNull
	@Size(min=3, max=15)
	private String state;
	
	@Column(name="STREET",length=40)	
	@NotNull
	@Size(min=3, max=40)
	private String street;
	
	@Column(name="DISTRICT",length=40)
	@NotNull
	@Size(min=3, max=40)
	private String district;
	
	@Column(name="ZIPCODE")	
	@NotNull
	@Min(value=1)
	private Integer zipCode;
	
	@Column(name="NUM")
	@NotNull	
	private Integer number;
	
	@Column(name="INTERIOR")
	private String interior;
	
	public Address(){
		
	}
	
	public Address(String country, String city, String state, String street, String district, Integer zipCode, Integer number) {
		super();
		this.country = country;
		this.city=city;
		this.state = state;
		this.street = street;
		this.district = district;
		zipCode = zipCode;
		this.number = number;
	}
	
	@Override
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCountry() {
		return country;
	}
	
	public void setCity(String city){
		this.city=city;
	}
	
	public String getCity(){
		return city;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public Integer getZipCode() {
		return zipCode;
	}

	public void setZipCode(Integer zipCode) {
		this.zipCode = zipCode;
	}

	public Integer getNumber() {
		return number;
	}

	public void setNumber(Integer number) {
		this.number = number;
	}

	public String getInterior() {
		return interior;
	}

	public void setInterior(String interior) {
		this.interior = interior;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((zipCode == null) ? 0 : zipCode.hashCode());
		result = prime * result + ((country == null) ? 0 : country.hashCode());
		result = prime * result + ((district == null) ? 0 : district.hashCode());		
		result = prime * result + ((interior == null) ? 0 : interior.hashCode());
		result = prime * result + ((number == null) ? 0 : number.hashCode());
		result = prime * result + ((state == null) ? 0 : state.hashCode());
		result = prime * result + ((street == null) ? 0 : street.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Address other = (Address) obj;
		if (zipCode == null) {
			if (other.zipCode != null)
				return false;
		} else if (!zipCode.equals(other.zipCode))
			return false;
		if (country == null) {
			if (other.country != null)
				return false;
		} else if (!country.equals(other.country))
			return false;
		if (district == null) {
			if (other.district != null)
				return false;
		} else if (!district.equals(other.district))
			return false;		
		if (interior == null) {
			if (other.interior != null)
				return false;
		} else if (!interior.equals(other.interior))
			return false;
		if (number == null) {
			if (other.number != null)
				return false;
		} else if (!number.equals(other.number))
			return false;
		if (state == null) {
			if (other.state != null)
				return false;
		} else if (!state.equals(other.state))
			return false;
		if (street == null) {
			if (other.street != null)
				return false;
		} else if (!street.equals(other.street))
			return false;
		return true;
	}
	
	

}

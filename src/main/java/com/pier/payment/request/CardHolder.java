package com.pier.payment.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class CardHolder {
	
	private String name;
	private Identification identification;
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Identification getIdentification() {
		return identification;
	}
	public void setIdentification(Identification identification) {
		this.identification = identification;
	}
	
	

}

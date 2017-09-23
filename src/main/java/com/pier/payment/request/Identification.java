package com.pier.payment.request;

public class Identification {
	
	private String number;
	private String type; //https://api.mercadopago.com/sites/:site_id/identification_types	
	
	public String getNumber() {
		return number;
	}
	public void setNumber(String number) {
		this.number = number;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	

}

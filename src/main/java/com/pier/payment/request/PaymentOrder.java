package com.pier.payment.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentOrder {
	
	public static String MERCADOLIBRE="mercadolibre";
	public static String MERCADOPAGO="mercadopago";
	
	public String type;
	public long id;
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	
	

}

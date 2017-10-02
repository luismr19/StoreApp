package com.pier.rest.model;

public class CheckoutRequest {
	
	private Address address;
	private String token;
	private String paymentMethod;
	private Integer issuer_id;
	private Integer installments;
	
	
	public Address getAddress() {
		return address;
	}
	public void setAddress(Address address) {
		this.address = address;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getPaymentMethod() {
		return paymentMethod;
	}
	public void setPaymentMethod(String paymentMethod) {
		this.paymentMethod = paymentMethod;
	}
	public Integer getIssuer_id() {
		return issuer_id;
	}
	public void setIssuer_id(Integer issuer_id) {
		this.issuer_id = issuer_id;
	}
	public Integer getInstallments() {
		return installments;
	}
	public void setInstallments(Integer installments) {
		this.installments = installments;
	}
	
	
	
	
}

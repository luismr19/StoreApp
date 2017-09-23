package com.pier.payment.request;

import java.time.LocalDateTime;

public class Payer {
	
	public static String ENT_TYPE_INDIVIDUAL="individual";
	public static String ENT_TYPE_ASSOCIATION="association";
	public static String TYPE_CUSTOMER="customer";
	public static String TYPE_REGISTERED="registered";
	public static String TYPE_GUEST="guest";
	
	private String entity_type;
	private String type;
	private String id;
	private String email;
	private String identification;
	private Phone phone;
	private String first_name;
	private String last_name;
	private PayerAddress address;
	private LocalDateTime registration_date;
	
	
	public static String getENT_TYPE_INDIVIDUAL() {
		return ENT_TYPE_INDIVIDUAL;
	}
	public static void setENT_TYPE_INDIVIDUAL(String eNT_TYPE_INDIVIDUAL) {
		ENT_TYPE_INDIVIDUAL = eNT_TYPE_INDIVIDUAL;
	}
	public static String getENT_TYPE_ASSOCIATION() {
		return ENT_TYPE_ASSOCIATION;
	}
	public static void setENT_TYPE_ASSOCIATION(String eNT_TYPE_ASSOCIATION) {
		ENT_TYPE_ASSOCIATION = eNT_TYPE_ASSOCIATION;
	}
	public static String getTYPE_CUSTOMER() {
		return TYPE_CUSTOMER;
	}
	public static void setTYPE_CUSTOMER(String tYPE_CUSTOMER) {
		TYPE_CUSTOMER = tYPE_CUSTOMER;
	}
	public static String getTYPE_REGISTERED() {
		return TYPE_REGISTERED;
	}
	public static void setTYPE_REGISTERED(String tYPE_REGISTERED) {
		TYPE_REGISTERED = tYPE_REGISTERED;
	}
	public static String getTYPE_GUEST() {
		return TYPE_GUEST;
	}
	public static void setTYPE_GUEST(String tYPE_GUEST) {
		TYPE_GUEST = tYPE_GUEST;
	}
	public String getEntity_type() {
		return entity_type;
	}
	public void setEntity_type(String entity_type) {
		this.entity_type = entity_type;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getIdentification() {
		return identification;
	}
	public void setIdentification(String identification) {
		this.identification = identification;
	}
	public Phone getPhone() {
		return phone;
	}
	public void setPhone(Phone phone) {
		this.phone = phone;
	}
	public String getFirst_name() {
		return first_name;
	}
	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}
	public String getLast_name() {
		return last_name;
	}
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	public PayerAddress getAddress() {
		return address;
	}
	public void setAddress(PayerAddress address) {
		this.address = address;
	}
	public LocalDateTime getRegistration_date() {
		return registration_date;
	}
	public void setRegistration_date(LocalDateTime registration_date) {
		this.registration_date = registration_date;
	}
	
	
	
}

package com.pier.payment.request;

public class Source {
	
	private String id;//User ID who issued the refund 
	private String name;//User who issued the refund
	private String type;//Type of user who issued the refund 
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	
	
	

}

package com.pier.payment.request;

import java.util.List;

public class AdditionalInfo {
	
	private String ip_address; //IP from where the request comes from (only for bank transfers)
	private List<Item> items;
	private Payer payer;
	private Shipments shipments;
	private Barcode barcode;
	
	
	public String getIp_address() {
		return ip_address;
	}
	public void setIp_address(String ip_address) {
		this.ip_address = ip_address;
	}
	public List<Item> getItems() {
		return items;
	}
	public void setItems(List<Item> items) {
		this.items = items;
	}
	public Payer getPayer() {
		return payer;
	}
	public void setPayer(Payer payer) {
		this.payer = payer;
	}
	
	

}

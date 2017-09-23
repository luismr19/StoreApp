package com.pier.payment.request;

import java.time.LocalDateTime;

public class Refund {
	
	private int id;
	private int payment_id;
	private float amount;
	private Object metadata;
	private Source source; //Who made the refund 
	private LocalDateTime date_created;
	private String unique_sequence_number;//Refund identifier given by the card processor 
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getPayment_id() {
		return payment_id;
	}
	public void setPayment_id(int payment_id) {
		this.payment_id = payment_id;
	}
	public float getAmount() {
		return amount;
	}
	public void setAmount(float amount) {
		this.amount = amount;
	}
	public Object getMetadata() {
		return metadata;
	}
	public void setMetadata(Object metadata) {
		this.metadata = metadata;
	}
	public Source getSource() {
		return source;
	}
	public void setSource(Source source) {
		this.source = source;
	}
	public LocalDateTime getDate_created() {
		return date_created;
	}
	public void setDate_created(LocalDateTime date_created) {
		this.date_created = date_created;
	}
	public String getUnique_sequence_number() {
		return unique_sequence_number;
	}
	public void setUnique_sequence_number(String unique_sequence_number) {
		this.unique_sequence_number = unique_sequence_number;
	}
	
	

}

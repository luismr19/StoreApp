package com.pier.payment.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class PaymentEvent {
	
	Integer id;
	String type;
	String action;
	String api_version;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
	LocalDateTime date_created;
	Integer user_id;
	Boolean live_mode;
	EventData data;
	
	public Integer getId() {
		return id;
	}
	public void setId(Integer id) {
		this.id = id;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getApi_version() {
		return api_version;
	}
	public void setApi_version(String api_version) {
		this.api_version = api_version;
	}
	public LocalDateTime getDate_created() {
		return date_created;
	}
	public void setDate_created(LocalDateTime date_created) {
		this.date_created = date_created;
	}
	public Integer getUser_id() {
		return user_id;
	}
	public void setUser_id(Integer user_id) {
		this.user_id = user_id;
	}
	public Boolean getLive_mode() {
		return live_mode;
	}
	public void setLive_mode(Boolean live_mode) {
		this.live_mode = live_mode;
	}
	public EventData getData() {
		return data;
	}
	public void setData(EventData data) {
		this.data = data;
	}
	

}



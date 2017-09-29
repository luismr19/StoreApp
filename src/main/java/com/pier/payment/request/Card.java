package com.pier.payment.request;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonFormat;

public class Card {
	
	private long id;
	private String last_four_digits;
	private String first_six_digits;
	private int expiration_year;
	private int expiration_month;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
	private LocalDateTime date_created;
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ")
	private LocalDateTime date_last_updated;
	private CardHolder cardholder;
	
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public String getLast_four_digits() {
		return last_four_digits;
	}
	public void setLast_four_digits(String last_four_digits) {
		this.last_four_digits = last_four_digits;
	}
	public String getFirst_six_digits() {
		return first_six_digits;
	}
	public void setFirst_six_digits(String first_six_digits) {
		this.first_six_digits = first_six_digits;
	}
	public int getExpiration_year() {
		return expiration_year;
	}
	public void setExpiration_year(int expiration_year) {
		this.expiration_year = expiration_year;
	}
	public int getExpiration_month() {
		return expiration_month;
	}
	public void setExpiration_month(int expiration_month) {
		this.expiration_month = expiration_month;
	}
	public LocalDateTime getDate_created() {
		return date_created;
	}
	public void setDate_created(LocalDateTime date_created) {
		this.date_created = date_created;
	}
	public LocalDateTime getDate_last_updated() {
		return date_last_updated;
	}
	public void setDate_last_updated(LocalDateTime date_last_updated) {
		this.date_last_updated = date_last_updated;
	}
	public CardHolder getCardholder() {
		return cardholder;
	}
	public void setCardholder(CardHolder cardholder) {
		this.cardholder = cardholder;
	}
	
	
	
	
	

}

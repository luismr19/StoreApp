package com.pier.rest.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;

import com.pier.model.security.User;

@Entity
@Table(name = "userproductcall")
public class UserProductCall {
	
	@Id
	@Column(name="ID")
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name="PRODUCT_ID")	
	@Fetch(FetchMode.SELECT)
	private Product product;
	
	@OneToOne
	@JoinColumn(name = "CALLER", referencedColumnName = "ID")
	@Fetch(FetchMode.SELECT)
	private User caller;
	
	@Column(name="STATUS")
	@Enumerated(EnumType.STRING)
	private ProductCallStatus status;
	
	@Column(name = "CALL_DATE", columnDefinition = "DATE")
	@Type(type = "org.hibernate.type.LocalDateType")
	private LocalDate callDate;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public User getCaller() {
		return caller;
	}

	public void setCaller(User caller) {
		this.caller = caller;
	}

	public ProductCallStatus getStatus() {
		return status;
	}

	public void setStatus(ProductCallStatus status) {
		this.status = status;
	}

	public LocalDate getCallDate() {
		return callDate;
	}

	public void setCallDate(LocalDate callDate) {
		this.callDate = callDate;
	}

	
	
		
	
	
	
}

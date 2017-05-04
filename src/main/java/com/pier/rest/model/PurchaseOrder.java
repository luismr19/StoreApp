package com.pier.rest.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;
import org.hibernate.annotations.Type;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.pier.model.security.User;

@Entity
@Table(name = "PURCHASE_ORDER")
public class PurchaseOrder implements ObjectModel<Long>{	

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@Column(name="TOTAL",nullable= false, precision=12, scale=2)    // Creates the database field with this size.
	@Digits(integer=12, fraction=2)  
	private BigDecimal total;
	
	@JoinColumn(name="GIFT")
	@OneToOne(fetch=FetchType.EAGER)
	@Cascade(CascadeType.ALL)	
	private Benefit gift;		
	
	@Column(name = "PURCHASE_TIME", columnDefinition="DATETIME")	
	@Type(type="org.hibernate.type.ZonedDateTimeType")
	private ZonedDateTime purchaseDate;
	
	@JoinColumn(name="ADDRESS",updatable=true)	
	@OneToOne(fetch=FetchType.EAGER)
	@Cascade({CascadeType.MERGE,CascadeType.SAVE_UPDATE})
	private Address deliveryAddress;
	
	@Column(name = "TRACK_NUMBER", length = 50, nullable=true)
	@NotNull
	@Size(min = 4, max = 50)
	private String trackingNumber;
	
	@OneToMany(mappedBy="id.order",fetch=FetchType.EAGER,orphanRemoval=true) //since id is the composite Key have to do notation like this
	@Cascade(CascadeType.ALL)
	@Fetch(FetchMode.SUBSELECT)//to remove join duplicates
	private Set<OrderDetail> orderDetails;	
	
	@ManyToOne
	@JoinColumn(name="OWNER" ,referencedColumnName="ID")
	private User owner;
	
	@Column(name="CONCLUDED")
	private Boolean concluded;
	
	public PurchaseOrder(){
		
	}	

	@Override
	public Long getId() {
		return id;
	}

	@JsonIgnore
	public void setId(Long id) {
		this.id = id;
	}

	public BigDecimal getTotal() {
		return total;
	}

	@JsonIgnore
	public void setTotal(BigDecimal total) {
		this.total = total;
	}

	public Benefit getGift() {
		return gift;
	}

	@JsonIgnore
	public void setGift(Benefit gift) {
		this.gift = gift;
	}

	public ZonedDateTime getPurchaseDate() {
		return purchaseDate;
	}
	@JsonIgnore
	public void setPurchaseDate(ZonedDateTime purchaseDate) {
		this.purchaseDate = purchaseDate;
	}

	public Address getDeliveryAddress() {
		return deliveryAddress;
	}
	@JsonIgnore
	public void setDeliveryAddress(Address deliveryAddress) {
		this.deliveryAddress = deliveryAddress;
	}

	public String getTrackingNumber() {
		return trackingNumber;
	}

	@JsonIgnore
	public void setTrackingNumber(String trackingNumber) {
		this.trackingNumber = trackingNumber;
	}

	public Set<OrderDetail> getPurchaseItems() {
		return orderDetails;
	}

	@JsonIgnore
	public void setPurchaseItems(Set<OrderDetail> purchaseItems) {
		this.orderDetails = purchaseItems;
		linkDetails();
	}

	public User getOwner() {
		return owner;
	}

	@JsonIgnore
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	private void linkDetails(){
		for(OrderDetail detail: orderDetails){
			detail.setOrder(this);
		}
	}
	
	public Boolean getConcluded() {
		return concluded;
	}

	@JsonIgnore
	public void setConcluded(Boolean concluded) {
		this.concluded = concluded;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((deliveryAddress == null) ? 0 : deliveryAddress.hashCode());				
		result = prime * result + ((owner == null) ? 0 : owner.hashCode());
		result = prime * result + ((purchaseDate == null) ? 0 : purchaseDate.hashCode());
		result = prime * result + ((total == null) ? 0 : total.hashCode());
		result = prime * result + ((trackingNumber == null) ? 0 : trackingNumber.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		PurchaseOrder other = (PurchaseOrder) obj;
		if (deliveryAddress == null) {
			if (other.deliveryAddress != null)
				return false;
		} else if (!deliveryAddress.equals(other.deliveryAddress))
			return false;
		if (gift == null) {
			if (other.gift != null)
				return false;
		} else if (!gift.equals(other.gift))
			return false;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		if (orderDetails == null) {
			if (other.orderDetails != null)
				return false;
		} else if (!orderDetails.equals(other.orderDetails))
			return false;
		if (owner == null) {
			if (other.owner != null)
				return false;
		} else if (!owner.equals(other.owner))
			return false;
		if (purchaseDate == null) {
			if (other.purchaseDate != null)
				return false;
		} else if (!purchaseDate.equals(other.purchaseDate))
			return false;
		if (total == null) {
			if (other.total != null)
				return false;
		} else if (!total.equals(other.total))
			return false;
		if (trackingNumber == null) {
			if (other.trackingNumber != null)
				return false;
		} else if (!trackingNumber.equals(other.trackingNumber))
			return false;
		return true;
	}
	
	

}

package com.pier.rest.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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

import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.Type;
import org.hibernate.type.LocalDateTimeType;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.pier.model.security.User;

@Entity
@Table(name = "PURCHASE_ORDER")
public class PurchaseOrder implements ObjectModel<Long>{  

  @Id
  @GeneratedValue(strategy=GenerationType.IDENTITY)  
  private Long id;
  
  @Column(name="TOTAL",nullable= false, precision=12, scale=2)    // Creates the database field with this size.
  @Digits(integer=12, fraction=2)    
  private BigDecimal total=new BigDecimal("0.00");
  
  @JoinColumn(name="GIFT")
  @OneToOne(fetch=FetchType.EAGER, orphanRemoval=true)
  @Cascade(CascadeType.ALL)   
  private Benefit gift;   
  
  @Column(name = "PURCHASE_TIME", columnDefinition="DATETIME")  
  @Type(type="org.hibernate.type.LocalDateTimeType")
  private LocalDateTime purchaseDate;
  
  @JoinColumn(name="ADDRESS",updatable=true)  
  @OneToOne(fetch=FetchType.EAGER)
  @Cascade({CascadeType.MERGE,CascadeType.SAVE_UPDATE})  
  private Address deliveryAddress;
  
  @Column(name = "TRACK_NUMBER", length = 50, nullable=true)
  @NotNull
  @Size(min = 4, max = 50)  
  private String trackingNumber;
  
  @OneToMany(mappedBy="id.order",fetch=FetchType.EAGER,orphanRemoval=true) //since id is the composite Key have to do notation like this
  @Cascade({CascadeType.SAVE_UPDATE,CascadeType.MERGE})
  @BatchSize(size=5)
  @Fetch(FetchMode.SELECT)//to remove join duplicates
  private Set<OrderDetail> orderDetails;  
  
  @ManyToOne
  @JoinColumn(name="OWNER" ,referencedColumnName="ID")
  @BatchSize(size=1)
  @Fetch(FetchMode.SELECT)
  private User owner;
  
  @Column(name="CONCLUDED")  
  private Boolean concluded;
  
  @Column(name="DELIVERED", columnDefinition = "boolean default false")  
  private Boolean delivered=false;
  
  @Column(name="REJECTED",columnDefinition = "boolean default false")  
  private Boolean rejected=false;
  
  public PurchaseOrder(){
    
  } 

  @Override
  @JsonProperty  
  public Long getId() {
    return id;
  }

  @JsonIgnore
  public void setId(Long id) {
    this.id = id;
  }

  @JsonProperty  
  public BigDecimal getTotal() {
    return total;
  }

  @JsonIgnore
  public void setTotal(BigDecimal total) {
    this.total = total;
  }

  @JsonProperty  
  public Benefit getGift() {
    return gift;
  }

  @JsonIgnore
  public void setGift(Benefit gift) {
    this.gift = gift;
  }

  @JsonProperty  
  @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss")
  public LocalDateTime getPurchaseDate() {
    return purchaseDate;
  }
  @JsonIgnore
  public void setPurchaseDate(LocalDateTime purchaseDate) {
    this.purchaseDate = purchaseDate;
  }

  @JsonProperty  
  public Address getDeliveryAddress() {
    return deliveryAddress;
  }
  @JsonIgnore
  public void setDeliveryAddress(Address deliveryAddress) {
    this.deliveryAddress = deliveryAddress;
  }

  @JsonProperty  
  public String getTrackingNumber() {
    return trackingNumber;
  }

  @JsonIgnore
  public void setTrackingNumber(String trackingNumber) {
    this.trackingNumber = trackingNumber;
  }

  @JsonProperty("orderDetails") 
  public Set<OrderDetail> getOrderDetails() {
    return orderDetails;
  }

  @JsonIgnore
  public void setOrderDetails(Set<OrderDetail> purchaseItems) {
    this.orderDetails = purchaseItems;
    linkDetails();
  }
  
  @JsonProperty
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

  
  public void setConcluded(Boolean concluded) {
    this.concluded = concluded;
  }
    

  public Boolean getRejected() {
	return rejected;
  }

  
  public void setRejected(Boolean rejected) {
	this.rejected = rejected;
  }

  public Boolean getDelivered() {
	return delivered;
  }

  public void setDelivered(Boolean delivered) {
	this.delivered = delivered;
  }

@Override
public int hashCode() {
	final int prime = 31;
	int result = 1;
	result = prime * result + ((concluded == null) ? 0 : concluded.hashCode());
	result = prime * result + ((delivered == null) ? 0 : delivered.hashCode());
	result = prime * result + ((deliveryAddress == null) ? 0 : deliveryAddress.hashCode());
	result = prime * result + ((gift == null) ? 0 : gift.hashCode());
	result = prime * result + ((owner == null) ? 0 : owner.hashCode());
	result = prime * result + ((purchaseDate == null) ? 0 : purchaseDate.hashCode());
	result = prime * result + ((rejected == null) ? 0 : rejected.hashCode());
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
	if (concluded == null) {
		if (other.concluded != null)
			return false;
	} else if (!concluded.equals(other.concluded))
		return false;
	if (delivered == null) {
		if (other.delivered != null)
			return false;
	} else if (!delivered.equals(other.delivered))
		return false;
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
	if (rejected == null) {
		if (other.rejected != null)
			return false;
	} else if (!rejected.equals(other.rejected))
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

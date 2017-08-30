package com.pier.rest.model;

import java.math.BigDecimal;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.Digits;

import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;
import org.hibernate.annotations.NotFound;
import org.hibernate.annotations.NotFoundAction;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name="BENEFIT")
public class Benefit implements ObjectModel<Long>{
	
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToMany(fetch=FetchType.EAGER)
	@JoinTable(name="BENEFIT_PRODUCT", joinColumns=@JoinColumn(name="BENEFIT_ID" ,referencedColumnName="ID"),
	inverseJoinColumns={@JoinColumn(name="PRODUCT_ID", referencedColumnName="PRODUCT_ID")})
	@Fetch(FetchMode.SELECT)
	private List<Product> products;
	
	@Column(name="DISCOUNT", precision=7, scale=2)    
	@Digits(integer=7, fraction=2) 
	private BigDecimal discount;
	
	@Column(name="POINTS")    
	@Digits(integer=7, fraction=2) 
	private Long points;
	
	@JoinColumn(name="PURCHASE_ORDER")
	@OneToOne(mappedBy="gift")
	@NotFound(action=NotFoundAction.IGNORE)	
	PurchaseOrder order;
	
	@Override
	public Long getId() {
	  return id;
	}

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public Long getPoints() {
		return points;
	}

	public void setPoints(Long points) {
		this.points = points;
	}

	public BigDecimal getDiscount() {
		return discount;
	}

	public void setDiscount(BigDecimal discount) {
		this.discount = discount;
	}
		
    @JsonIgnore
	public PurchaseOrder getOrder() {
		return order;
	}

	
	public void setOrder(PurchaseOrder order) {
		this.order = order;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((discount == null) ? 0 : discount.hashCode());
		result = prime * result + ((points == null) ? 0 : points.hashCode());
		result = prime * result + ((products == null) ? 0 : products.hashCode());
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
		Benefit other = (Benefit) obj;
		if (discount == null) {
			if (other.discount != null)
				return false;
		} else if (!discount.equals(other.discount))
			return false;
		if (points == null) {
			if (other.points != null)
				return false;
		} else if (!points.equals(other.points))
			return false;
		if (products == null) {
			if (other.products != null)
				return false;
		} else if (!products.equals(other.products))
			return false;
		return true;
	}
	
	

}

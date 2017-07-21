package com.pier.rest.model;

import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
@Table(name = "ORDER_DETAIL")
public class OrderDetail implements ObjectModel<OrderDetailId> {

	@EmbeddedId
	OrderDetailId id;

	@Column(name = "QUANTITY")
	private Integer quantity;

	/*
	 * @ManyToOne //this is like using @MapsId
	 * 
	 * @JoinColumn(name = "ORDER_ID",referencedColumnName="ID",
	 * insertable=false, updatable=false) private Order order;
	 */	

	public OrderDetail(Integer quantity, ProductFlavor product, PurchaseOrder order) {
		super();
		id=new OrderDetailId();
		this.quantity = quantity;
		setProduct(product);
		setOrder(order);
	}

	public OrderDetail() {
		super();		
		id=new OrderDetailId();
	}
	
	@Override
	@JsonIgnore
	public OrderDetailId getId() {
		return id;
	}

	public void setId(OrderDetailId id) {
		this.id = id;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public ProductFlavor getProduct() {
		return this.id.getProduct();
	}

	public void setProduct(ProductFlavor product) {
		this.id.setProduct(product);
	}

	@JsonIgnore
	public PurchaseOrder getOrder() {
		return this.id.getOrder();
	}

	public void setOrder(PurchaseOrder order) {
		this.id.setOrder(order);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((quantity == null) ? 0 : quantity.hashCode());
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
		OrderDetail other = (OrderDetail) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		}
		if (quantity == null) {
			if (other.quantity != null)
				return false;
		} else if (!quantity.equals(other.quantity))
			return false;
		return true;
	}
	
	

}

package com.pier.rest.model;

import java.util.Set;

import com.pier.model.security.User;

public class UserOrder {
	
	private User user;
	
	private Set<PurchaseOrder> purchaseOrders;
	
	

	public UserOrder(User user, Set<PurchaseOrder> orders) {
		super();
		this.user = user;
		this.purchaseOrders = orders;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<PurchaseOrder> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(Set<PurchaseOrder> orders) {
		this.purchaseOrders = orders;
	}
	
	

}
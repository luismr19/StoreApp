package com.pier.rest.model;

import java.util.List;
import java.util.Set;

import com.pier.model.security.User;

public class UserOrder {
	
	private User user;
	
	private List<PurchaseOrder> purchaseOrders;
	
	

	public UserOrder(User user, List<PurchaseOrder> orders) {
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

	public List<PurchaseOrder> getPurchaseOrders() {
		return purchaseOrders;
	}

	public void setPurchaseOrders(List<PurchaseOrder> orders) {
		this.purchaseOrders = orders;
	}
	
	

}

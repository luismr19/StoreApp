package com.pier.rest.model;

import java.util.Set;

import com.pier.model.security.User;

public class UserOrder {
	
	private User user;
	
	private Set<PurchaseOrder> orders;
	
	

	public UserOrder(User user, Set<PurchaseOrder> orders) {
		super();
		this.user = user;
		this.orders = orders;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public Set<PurchaseOrder> getOrder() {
		return orders;
	}

	public void setOrder(Set<PurchaseOrder> order) {
		this.orders = order;
	}
	
	

}

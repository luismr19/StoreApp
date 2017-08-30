package com.pier.service.impl;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pier.rest.model.PurchaseOrder;
import com.pier.service.PurchaseOrderDao;

@Transactional
@Component
public class OrderService {
	
	@Autowired
	PurchaseOrderDao orderDao;

	public void clearBenefit(PurchaseOrder order){
		Query deleteGift=orderDao.currentSession().createQuery("update PurchaseOrder set gift =:gift where id=:id");
		deleteGift.setParameter("gift", null);
		deleteGift.setParameter("id", order.getId());
		
		order.setGift(null);
		
		deleteGift.executeUpdate();
	}
}

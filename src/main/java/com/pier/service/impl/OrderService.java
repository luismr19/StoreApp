package com.pier.service.impl;

import java.util.List;

import javax.persistence.NoResultException;

import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pier.model.security.User;
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
		order.getGift().setOrder(null);
		order.setGift(null);
		
		deleteGift.executeUpdate();
	}
	
	public PurchaseOrder getCart(User user){
		try{
		Query<PurchaseOrder> cartQuery=orderDao.currentSession().createQuery("select pOrder from PurchaseOrder pOrder where pOrder.concluded=false and pOrder.owner.id=:id");
		cartQuery.setParameter("id", user.getId());
		
		return cartQuery.getSingleResult();
		}catch(NoResultException e){
			return null;
		}
	}
	
	public List<PurchaseOrder> getOrderHistory(String username){
		Query<PurchaseOrder> cartQuery=orderDao.currentSession().createQuery("select pOrder from PurchaseOrder pOrder where pOrder.concluded=true and pOrder.owner.username=:username order by pOrder.purchaseDate");
		cartQuery.setParameter("username", username);
		
		return cartQuery.getResultList();
	}
}

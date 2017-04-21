package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.PurchaseOrder;
import com.pier.service.PurchaseOrderDao;

@Repository("purchaseOrderDao")
public class PurchaseOrderDaoImpl extends HibernateDao<PurchaseOrder, Long> implements PurchaseOrderDao {

	@Override
	public boolean removePurchaseOrder(PurchaseOrder order) {
		delete(order);
		return true;
	}

}

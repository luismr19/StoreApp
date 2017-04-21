package com.pier.service;

import com.pier.rest.model.PurchaseOrder;

public interface PurchaseOrderDao extends GenericDao<PurchaseOrder, Long> {
	
	boolean removePurchaseOrder(PurchaseOrder order);

}

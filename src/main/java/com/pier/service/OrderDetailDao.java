package com.pier.service;

import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.OrderDetailId;

public interface OrderDetailDao extends GenericDao<OrderDetail, OrderDetailId> {
	
	boolean removeOrderdetail(OrderDetail orderDetail);

}

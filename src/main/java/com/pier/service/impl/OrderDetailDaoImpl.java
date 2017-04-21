package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.OrderDetailId;
import com.pier.service.OrderDetailDao;

@Repository("orderDetailDao")
public class OrderDetailDaoImpl extends HibernateDao<OrderDetail,OrderDetailId> implements OrderDetailDao {

	@Override
	public boolean removeOrderdetail(OrderDetail orderDetail) {
		delete(orderDetail);
		
		return true;
	}


}

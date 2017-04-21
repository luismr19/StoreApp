package com.pier.service.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Date;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pier.DomainAwareBase;
import com.pier.rest.model.Address;
import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.OrderDetailId;
import com.pier.rest.model.Product;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.OrderDetailDao;
import com.pier.service.ProductDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

public class OrderDetailDaoImplTest extends DomainAwareBase {

	@Autowired
	OrderDetailDao orderDetailDao;

	@Autowired
	private UserDao userDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	private PurchaseOrderDao orderDao;

	@Test
	public void testRemoveOrderdetail() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		OrderDetail orderDetail = new OrderDetail();

		// prepare order
		PurchaseOrder order = new PurchaseOrder();
		order.setDeliveryAddress(new Address("USA", "Michigan", "theStreet", "porai", 4487, 14));
		order.setOwner(userDao.find(1L));
		Date date = new Date();
		Timestamp timestamp = new Timestamp(date.getTime());
		order.setPurchaseDate(timestamp);		
		order.setTrackingNumber("RT784512W");
		order.setTotal(new BigDecimal("100.00"));
		orderDao.add(order);

		// prepare product

		Product product1 = new Product();

		product1.setExistence(4L);
		product1.setEnabled(true);
		product1.setName("nitrotech");
		product1.setDescription("whey protein isolate");
		product1.setPrice(new BigDecimal("100.00"));
		productDao.add(product1);

		
		//prepare order detail
		
		orderDetail.setOrder(order);
		orderDetail.setProduct(product1);
		orderDetail.setQuantity(2);
		
		//update total
		order.setTotal(orderDetail.getProduct().getPrice().multiply(new BigDecimal(orderDetail.getQuantity())));
		orderDao.update(order);
		
		orderDetailDao.add(orderDetail);
		OrderDetail newDetail=orderDetailDao.find(orderDetail.getId());
		assertTrue(newDetail.equals(orderDetail));

	}

	@Test
	public void testUpdate() {
		
		        OrderDetail orderDetail = new OrderDetail();
		        
		       // prepare order
				PurchaseOrder order = new PurchaseOrder();
				order.setDeliveryAddress(new Address("USA", "Michigan", "theStreet", "porai", 4487, 14));
				order.setOwner(userDao.find(1L));
				Date date = new Date();
				Timestamp timestamp = new Timestamp(date.getTime());
				order.setPurchaseDate(timestamp);		
				order.setTrackingNumber("RT784512W");
				order.setTotal(new BigDecimal("100.00"));
				orderDao.add(order);

				// prepare product

				Product product1 = new Product();

				product1.setExistence(4L);
				product1.setEnabled(true);
				product1.setName("nitrotech");
				product1.setDescription("whey protein isolate");
				product1.setPrice(new BigDecimal("100.00"));
				productDao.add(product1);

				
				//prepare order detail
				
				orderDetail.setOrder(order);
				orderDetail.setProduct(product1);
				orderDetail.setQuantity(2);
				
				//update total
				order.setTotal(orderDetail.getProduct().getPrice().multiply(new BigDecimal(orderDetail.getQuantity())));
				orderDao.update(order);
				
				orderDetailDao.add(orderDetail);
				
				orderDetail.setQuantity(3);
				orderDetailDao.update(orderDetail);
				
				assertTrue(orderDetailDao.find(orderDetail.getId()).getQuantity().equals(3));
	}

	@Test
	public void testDelete() {
		fail("Not yet implemented");
	}

	@Test
	public void testList() {
		fail("Not yet implemented");
	}

	@Test
	public void testFindStringString() {
		fail("Not yet implemented");
	}

}

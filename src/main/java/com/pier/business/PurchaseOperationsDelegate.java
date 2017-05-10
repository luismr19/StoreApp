package com.pier.business;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.pier.business.exception.OutOfStockException;
import com.pier.business.util.OrderDetailUtil;
import com.pier.model.security.User;
import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.Product;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.ProductDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

public class PurchaseOperationsDelegate {

	@Autowired
	UserDao userDao;

	@Autowired
	PurchaseOrderDao orderDao;

	@Autowired
	ProductDao productDao;

	@Autowired
	CartOperationsDelegate cartOps;

	public void checkout(User user, PurchaseOrder newOrder) throws OutOfStockException {

		PurchaseOrder cart = cartOps.getUserCart(user);

		// check existence before buying
		for (OrderDetail detail : cart.getPurchaseItems()) {
			if (detail.getQuantity() > productDao.find(detail.getProduct().getId()).getExistence()) {
				throw new OutOfStockException(detail.getProduct().getName() + " is out of stock");
			}
		}

		cart.setDeliveryAddress(newOrder.getDeliveryAddress());
		cart.setPurchaseDate(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/Mexico_City")));
		orderDao.update(cart);

	}

	public void completeOrder(User user) {
		PurchaseOrder cart = cartOps.getUserCart(user);
		cart.setConcluded(true);
		List<Product> purchasedProducts = OrderDetailUtil.getAsProductList(cart.getPurchaseItems());

		for (Product product : purchasedProducts) {
			product.setExistence(product.getExistence() - 1);
			productDao.update(product);
		}
		orderDao.update(cart);

	}

}

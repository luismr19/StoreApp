package com.pier.business;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pier.business.exception.OutOfStockException;
import com.pier.business.util.OrderDetailUtil;
import com.pier.model.security.User;
import com.pier.rest.model.Address;
import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.ProductFlavorDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

@Component
public class PurchaseOperationsDelegate {

	@Autowired
	UserDao userDao;

	@Autowired
	PurchaseOrderDao orderDao;

	@Autowired
	ProductFlavorDao productFlavorDao;

	@Autowired
	CartOperationsDelegate cartOps;

	public void checkout(User user, Address deliveryAddress) throws OutOfStockException {

		PurchaseOrder cart = cartOps.getUserCart(user);

		// check existence before buying
		for (OrderDetail detail : cart.getPurchaseItems()) {
			if (detail.getQuantity() > productFlavorDao.find(detail.getProduct().getId()).getExistence()) {
				throw new OutOfStockException(detail.getProduct().getProduct().getName() + " is out of stock for that flavor");
			}
		}

		cart.setDeliveryAddress(deliveryAddress);
		cart.setPurchaseDate(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/Mexico_City")));
		orderDao.update(cart);
		//call credit card api here "maybe"

	}

	public void completeOrder(User user) {
		PurchaseOrder cart = cartOps.getUserCart(user);
		cart.setConcluded(true);
		List<ProductFlavor> purchasedProducts = OrderDetailUtil.getAsProductList(cart.getPurchaseItems());

		for (ProductFlavor product : purchasedProducts) {
			product.setExistence(product.getExistence() - 1);
			productFlavorDao.update(product);
		}
		orderDao.update(cart);

	}

}

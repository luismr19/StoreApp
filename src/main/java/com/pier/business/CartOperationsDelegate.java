package com.pier.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import com.pier.business.exception.OutOfStockException;
import com.pier.business.util.OrderDetailUtil;
import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.ProductDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

@Component
public class CartOperationsDelegate {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PurchaseOrderDao orderDao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	PromotionsAppliance promotionsAppliance;
	
	public void addToCart(User user,ProductFlavor product) throws OutOfStockException{
		addToCart(user,product,1);
	}
	
	public void addToCart(User user,ProductFlavor product, int quantity) throws OutOfStockException{
		PurchaseOrder cart=getUserCart(user);	
		
			if(OrderDetailUtil.mapToOrder(product, cart, quantity)){
				userDao.update(user);
				orderDao.update(cart);			
			}else{
			throw new OutOfStockException("out of stock");
			}
	}
	
	public void removeFromCart(User user, Long productId) throws OutOfStockException{
		PurchaseOrder cart=getUserCart(user);
		Product product=productDao.find(productId);
		OrderDetailUtil.removeProductFromDetails(cart.getPurchaseItems(), product);
		orderDao.update(cart);		
			
	}
			
	
	public PurchaseOrder getUserCart(User user){
	       PurchaseOrder cart=new PurchaseOrder();	
	       cart.setTrackingNumber("PENDING");
	       cart.setConcluded(false);
			if(user.getOrders()==null){			
				user.setOrders(new HashSet(Arrays.asList(cart)));
			}else{
			Optional<PurchaseOrder> pendingOrder=user.getOrders().stream()
					.filter(order->order.getConcluded()==false).findFirst();		
			if(pendingOrder.isPresent()){
				cart=pendingOrder.get();
			}else{
				user.getOrders().add(cart);
			}
			}
			cart.setOwner(user);
			//ApplyPromotions if any
			if(cart.getPurchaseItems()!=null && cart.getPurchaseItems().size()>0){
			if(PromotionsAppliance.isPromotionApplied(promotionsAppliance.calculateBenefits(cart))){
			cart.setGift(promotionsAppliance.calculateBenefits(cart));
			cart.getGift().setOrder(cart);
			}
			}
			
			return cart;
		}

}

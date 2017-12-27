package com.pier.business;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.pier.business.exception.OutOfStockException;
import com.pier.business.util.OrderDetailUtil;
import com.pier.model.security.User;
import com.pier.rest.model.Benefit;
import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.OrderDetailDao;
import com.pier.service.ProductDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;
import com.pier.service.impl.OrderService;
import com.pier.service.impl.FlavorService;

/**
 * @author Daniel Gz
 * 
 * this class is like a controller delegate to handle and manipulate complex logic outside the controller
 */
@Component
public class CartOperationsDelegate {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	OrderDetailDao detailDao;
	
	@Autowired
	PurchaseOrderDao orderDao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	PromotionsAppliance promotionsAppliance;
	
	@Autowired
	FlavorService flavorSvc;
	
	@Autowired
    OrderService cartService;
	
	@Value("${delivery_cost}")
	String delivery_cost;
	
	public PurchaseOrder addToCart(User user,ProductFlavor product) throws OutOfStockException{
		return addToCart(user,product,1);
	}
	
	public PurchaseOrder addToCart(User user,ProductFlavor product, int quantity) throws OutOfStockException{
		PurchaseOrder cart=getUserCart(user);	
		
		//mapToOrder takes care of the adding and quantities manipulation aswell as calculating the totals
			if(OrderDetailUtil.mapToOrder(product, cart, quantity)!=null){
				orderDao.update(cart);							
			}else{
			throw new OutOfStockException("out of stock");
			}			
			applyPromotions(cart);
			return cart;
	}
	
	public PurchaseOrder removeFromCart(User user, ProductFlavor product){
		PurchaseOrder cart=getUserCart(user);			
		OrderDetail detail=OrderDetailUtil.removeProductFromDetails(cart.getOrderDetails(), product);
		if(detail.getQuantity()<=0){
			//we do this because hibernate won't remove the objects from the set in their implementation in certain cases
			Set purchaseItems=new HashSet(cart.getOrderDetails());
			cart.getOrderDetails().clear();
			//method removeDetail basically creates a new collection without the specified detail
			cart.setOrderDetails(OrderDetailUtil.removeDetail(purchaseItems, detail));
		}
		
		cart.setTotal(OrderDetailUtil.updateTotals(cart));
		applyPromotions(cart);
		orderDao.update(cart);
		
		return cart;
			
	}
	
	public PurchaseOrder updateQuantities(User user, List<OrderDetail> updatedDetails){
		PurchaseOrder cart=getUserCart(user);
		Set<OrderDetail> newOrderDetails=OrderDetailUtil.updateOrderDetailsQuantities(cart.getOrderDetails(), updatedDetails);
		cart.setOrderDetails(newOrderDetails);
		
		cart.setTotal(OrderDetailUtil.updateTotals(cart));
		applyPromotions(cart);
		orderDao.update(cart);
		
		return cart;
	}
	
	public PurchaseOrder applyPromotions(PurchaseOrder cart){
		if(cart.getOrderDetails()!=null && cart.getOrderDetails().size()>0){
			//first try to see if some promotion can be applied
		if(PromotionsAppliance.isPromotionApplied(promotionsAppliance.calculateBenefits(cart))){
			//if something can be applied then add it
		cart.setGift(promotionsAppliance.calculateBenefits(cart));
		cart.getGift().setOrder(cart);
		}else{
			//if no promotion was applied then delete any previous promotions
			if(cart.getGift()!=null){
				cartService.clearBenefit(cart);					
			}
		}
		}else{
			if(cart.getGift()!=null)
			cartService.clearBenefit(cart);	
		}
		
		return cart;
		
	}
	
	public PurchaseOrder applyPromotionsReadOnly(PurchaseOrder cart){
		//security: reset any promotion first
		cart.setGift(new Benefit());
		if(cart.getOrderDetails()!=null && cart.getOrderDetails().size()>0){
			//first try to see if some promotion can be applied
		if(PromotionsAppliance.isPromotionApplied(promotionsAppliance.calculateBenefits(cart))){
			//if something can be applied then add it
		cart.setGift(promotionsAppliance.calculateBenefits(cart));
		cart.getGift().setOrder(cart);
		}
		}
		
		return cart;
		
	}
			
	
	public PurchaseOrder getUserCart(User user){
	       PurchaseOrder cart=new PurchaseOrder();	
	       cart.setTrackingNumber("PENDING");
	       cart.setConcluded(false);	       
			if(user.getOrders()==null){
				//okay no orders then create the cart for our buddy
				cart.setOwner(user);
				orderDao.add(cart);
				user.setOrders(new HashSet(Arrays.asList(cart)));
			}else{
			PurchaseOrder pendingOrder=cartService.getCart(user);	
			if(pendingOrder!=null){
				cart=pendingOrder;
				cart.setOwner(user);
			}else{
				//okay there are orders but none of them is pending
				cart.setOwner(user);
				orderDao.add(cart);
				/*Hibernate.initialize(user.getOrders());
				user.getOrders().add(cart);*/
			}
			}			
			//ApplyPromotions if any
			applyPromotions(cart);
			//by doing this we ensure that the delivery cost is always up to date inc ase there are carts created before an update in the delivery cost
			cart.setDeliveryCost(new BigDecimal(delivery_cost));
			return cart;
		}

}

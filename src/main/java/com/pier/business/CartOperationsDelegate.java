package com.pier.business;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pier.business.exception.OutOfStockException;
import com.pier.business.util.OrderDetailUtil;
import com.pier.model.security.User;
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
	
	public PurchaseOrder addToCart(User user,ProductFlavor product) throws OutOfStockException{
		return addToCart(user,product,1);
	}
	
	public PurchaseOrder addToCart(User user,ProductFlavor product, int quantity) throws OutOfStockException{
		PurchaseOrder cart=getUserCart(user);	
		
		//mapToOrder takes care of the adding and quantities manipulation aswell as calculating the totals
			if(OrderDetailUtil.mapToOrder(product, cart, quantity)!=null){
				userDao.update(user);							
			}else{
			throw new OutOfStockException("out of stock");
			}			
			applyPromotions(cart);
			return cart;
	}
	
	public PurchaseOrder removeFromCart(User user, ProductFlavor product) throws OutOfStockException{
		PurchaseOrder cart=getUserCart(user);			
		OrderDetail detail=OrderDetailUtil.removeProductFromDetails(cart.getPurchaseItems(), product);
		if(detail.getQuantity()<=0){
			//we do this because hibernate won't remove the objects from the set in their implementation in certain cases
			Set purchaseItems=new HashSet(cart.getPurchaseItems());
			cart.getPurchaseItems().clear();
			//method removeDetail basically creates a new collection without the specified detail
			cart.setPurchaseItems(OrderDetailUtil.removeDetail(purchaseItems, detail));
		}
		
		cart.setTotal(OrderDetailUtil.updateTotals(cart));
		applyPromotions(cart);
		userDao.update(user);
		
		return cart;
			
	}
	
	public PurchaseOrder updateQuantities(User user, List<OrderDetail> updatedDetails){
		PurchaseOrder cart=getUserCart(user);
		Set<OrderDetail> newOrderDetails=OrderDetailUtil.updateOrderDetailsQuantities(cart.getPurchaseItems(), updatedDetails);
		cart.setPurchaseItems(newOrderDetails);
		
		cart.setTotal(OrderDetailUtil.updateTotals(cart));
		applyPromotions(cart);
		userDao.update(user);
		
		return cart;
	}
	
	public void applyPromotions(PurchaseOrder cart){
		if(cart.getPurchaseItems()!=null && cart.getPurchaseItems().size()>0){
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
		}
		
	}
			
	
	public PurchaseOrder getUserCart(User user){
	       PurchaseOrder cart=new PurchaseOrder();	
	       cart.setTrackingNumber("PENDING");
	       cart.setConcluded(false);
			if(user.getOrders()==null){
				//okay no orders then create the cart for our buddy
				orderDao.add(cart);
				user.setOrders(new HashSet(Arrays.asList(cart)));
			}else{
			Optional<PurchaseOrder> pendingOrder=user.getOrders().stream()
					.filter(order->order.getConcluded()==false).findFirst();		
			if(pendingOrder.isPresent()){
				cart=pendingOrder.get();
			}else{
				//okay there are orders but none of them is pending
				orderDao.add(cart);
				user.getOrders().add(cart);
			}
			}
			cart.setOwner(user);
			//ApplyPromotions if any
			applyPromotions(cart);
			
			return cart;
		}

}

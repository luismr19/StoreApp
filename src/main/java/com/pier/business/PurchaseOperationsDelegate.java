package com.pier.business;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.mercadopago.MP;
import com.pier.business.exception.EmptyCartException;
import com.pier.business.exception.OutOfStockException;
import com.pier.business.util.AddressValidationUtil;
import com.pier.business.util.OrderDetailUtil;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import com.pier.model.security.User;
import com.pier.payment.PaymentUtils;
import com.pier.payment.request.Payment;
import com.pier.payment.request.PaymentEvent;
import com.pier.payment.request.ShippingOptionsRequest;
import com.pier.payment.request.Statuses;
import com.pier.rest.model.Address;
import com.pier.rest.model.CheckoutRequest;
import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.ProductFlavorDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;
import com.pier.service.impl.OrderService;

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
	
	@Autowired
	PaymentUtils paymentUtils;
	
	@Autowired
	OrderService orderService;
	
	@Value("${access_token}")
	String access_token;
	
	@Value("${ENVIRONMENT}")
	String environment;
	
	public PurchaseOrder checkout(User user, CheckoutRequest checkoutInfo) throws OutOfStockException,EmptyCartException, PaymentException, PaymentErrorException {
		PurchaseOrder cart = cartOps.getUserCart(user);
        
		int index=0;
		
		if(!AddressValidationUtil.isAddressValid(checkoutInfo.getAddress())){
			throw new PaymentErrorException("address is incorrect"); 
		}
		
		// check existence before buying
		for (OrderDetail detail : cart.getOrderDetails()) {
			index++;
			if (detail.getQuantity() > productFlavorDao.find(detail.getProduct().getId()).getExistence()) {				
				throw new OutOfStockException(detail.getProduct().getProduct().getName() + " is out of stock for that flavor");
			}
		}
		
		if(index==0)
			throw new EmptyCartException("Cart is empty");		
		
		Payment payment=null;
		//payment api
		if(checkoutInfo.getIssuer_id()>-1 && checkoutInfo.getInstallments()>1){
			payment=paymentUtils.pay(cart, checkoutInfo.getToken(), checkoutInfo.getPaymentMethod(),checkoutInfo.getIssuer_id(),checkoutInfo.getInstallments());
		}else{
			payment=paymentUtils.pay(cart, checkoutInfo.getToken(), checkoutInfo.getPaymentMethod());
		}
		
		System.out.println("ONEEE");
		//if payment request was successful
		if(payment!=null){
		cart.setPaymentId(payment.getId());
		//if status is approved
		if(payment.getStatus().equals(Statuses.approved)){  
		 this.updateExistence(cart);			
		 completeOrder(cart,checkoutInfo.getAddress());
		 //else if status is in process or pending
		}else if(payment.getStatus().equals(Statuses.authorized) || payment.getStatus().equals(Statuses.in_process) || payment.getStatus().equals(Statuses.pending)){
		 //only update existence and address but do not set the order as concluded
			preCompleteOrder(cart,checkoutInfo);		 
		}else{		
			throw new PaymentException(payment.getStatus_detail());
		}
		
		}else{
			throw new PaymentErrorException("default");
		}		
		return cart;
	
	}
	
	private void preCompleteOrder(PurchaseOrder cart, CheckoutRequest checkoutInfo){
		cart.setDeliveryAddress(checkoutInfo.getAddress());		
		this.updateExistence(cart);
		orderDao.update(cart);		
	}

	public PurchaseOrder completeOrder(User user) {
		PurchaseOrder cart = cartOps.getUserCart(user);		
		List<ProductFlavor> purchasedProducts = OrderDetailUtil.getAsProductList(cart.getOrderDetails());

		for (ProductFlavor product : purchasedProducts) {
			product.setExistence(product.getExistence() - 1);
			productFlavorDao.update(product);			
		}
		cart.setConcluded(true);
		orderDao.update(cart);
		
		return cart;

	}
	
	public void completeOrder(PurchaseOrder cart,Address deliveryAddress) {			
		
		cart.setDeliveryAddress(deliveryAddress);
		cart.setPurchaseDate(LocalDateTime.now());
		cart.setConcluded(true);
		orderDao.update(cart);

	}
	
	
	public boolean handleNotification(PaymentEvent event) throws Exception{
		
		PurchaseOrder order=paymentUtils.handleNotification(event);
		if(order!=null){
			orderDao.update(order);
			return true;
		}else{
			return false;
		}
	}
		
	
	public void updateExistence(PurchaseOrder cart){
		List<ProductFlavor> purchasedProducts = OrderDetailUtil.getAsProductList(cart.getOrderDetails());

		for (ProductFlavor product : purchasedProducts) {
			product.setExistence(product.getExistence() - 1);
			productFlavorDao.update(product);			
		}
	}

	public String calculateShippingOptions(ShippingOptionsRequest shippingOptionsRequest) {
		Map<String,Object> params=new HashMap<>();
		params.put("dimensions", shippingOptionsRequest.getDimensions());
		params.put("zip_code", shippingOptionsRequest.getZip_code());
		params.put("item_price", shippingOptionsRequest.getItem_price());
		
		return	paymentUtils.getShippingOptions(params);
		
	}

}

package com.pier.controllers.user;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.pier.business.PromotionsAppliance;
import com.pier.model.security.User;
import com.pier.rest.model.PurchaseOrder;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.ProductDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

public class PurchaseRestController {
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
		
	@Autowired
	UserDao userDao;
	
	@Autowired
	PurchaseOrderDao orderDao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	PromotionsAppliance promotionsAppliance;
	
	@RequestMapping(value="checkout",method=RequestMethod.PUT)
	public ResponseEntity<String> checkout(@RequestBody PurchaseOrder newOrder, HttpServletRequest request){
		
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
				
		PurchaseOrder cart=getUserCart(user);
		cart.setConcluded(true);
		try{
		cart.setDeliveryAddress(newOrder.getDeliveryAddress());		
		cart.setPurchaseDate(ZonedDateTime.of(LocalDateTime.now(), ZoneId.of("America/Mexico_City")));
		//get benefit again
		cart.setGift(promotionsAppliance.calculateBenefits(cart));
		orderDao.update(cart);
		}catch(Exception e){
			return new ResponseEntity<String>("error performing operation",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("success",HttpStatus.OK);
	}
	
	@RequestMapping(value="completePurchase",method=RequestMethod.PUT)
	public ResponseEntity<String> completeOrder(HttpServletRequest request){
		
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
				
		PurchaseOrder cart=getUserCart(user);
		cart.setConcluded(true);
		try{
		orderDao.update(cart);
		}catch(Exception e){
			return new ResponseEntity<String>("error perdorming operation",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<String>("success",HttpStatus.OK);
	}
	
	private PurchaseOrder getUserCart(User user){
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
			cart.setGift(promotionsAppliance.calculateBenefits(cart));
			
			return cart;
		}

}

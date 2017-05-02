package com.pier.controllers.user;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pier.business.PromotionsAppliance;
import com.pier.business.util.OrderDetailUtil;
import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.rest.model.PurchaseOrder;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.ProductDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

@RestController
public class CartRestController {
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PurchaseOrderDao orderDao;
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	PromotionsAppliance promotionsAppliance;
	
	
	@RequestMapping(value="addToCart",method=RequestMethod.POST)
	public ResponseEntity<String> addToCart(@RequestBody Product product,HttpServletRequest request){
		
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
				
		PurchaseOrder cart=getUserCart(user);
		try{
		if(OrderDetailUtil.mapToOrder(product, cart)){
			userDao.update(user);
			orderDao.update(cart);			
		}else{
		return new ResponseEntity<String>("Error adding product",HttpStatus.OK);
		}
		}catch(Exception e){
			return new ResponseEntity<String>("Error adding product",HttpStatus.CONFLICT);	
		}
		return new ResponseEntity<String>("successfully added to Cart",HttpStatus.OK);
		
	}
	
	
	
	@RequestMapping(value="removeFromCart",method=RequestMethod.GET)
	public ResponseEntity<String> removeFromCart(@RequestParam("id") Long id, HttpServletRequest request){
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
		PurchaseOrder cart=getUserCart(user);
		try{
		OrderDetailUtil.removeProductFromDetails(cart.getPurchaseItems(), productDao.find(id));
		orderDao.update(cart);
		}catch(Exception e){
			return new ResponseEntity<String>("unable to remove product",HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<String>("successfully removed",HttpStatus.GONE);
		
		
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

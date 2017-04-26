package com.pier.business;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
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
import org.springframework.web.bind.annotation.RestController;
import com.pier.business.util.OrderDetailUtil;
import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.rest.model.PurchaseOrder;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

@RestController
public class AddToCartRestController {
	
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
	
	@RequestMapping(value="addToCart",method=RequestMethod.POST)
	public ResponseEntity<String> addToCart(@RequestBody Product product,HttpServletRequest request){
		
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
				
		PurchaseOrder cart=getUserCart(user);
		
		if(OrderDetailUtil.mapToOrder(product, cart)){
			userDao.update(user);
			orderDao.update(cart);	
		return new ResponseEntity<String>("successfully added to Cart",HttpStatus.OK);
		}
		return new ResponseEntity<String>("Error adding product",HttpStatus.OK);
		
	}
	
	private PurchaseOrder getUserCart(User user){
       PurchaseOrder theOrder=new PurchaseOrder();	
		
		if(user.getOrders()==null){			
			user.setOrders(new HashSet(Arrays.asList(theOrder)));
		}else{
		Optional<PurchaseOrder> pendingOrder=user.getOrders().stream()
				.filter(order->order.getConcluded()==false).findFirst();		
		if(pendingOrder.isPresent()){
			theOrder=pendingOrder.get();
		}else{
			user.getOrders().add(theOrder);
		}
		}
		theOrder.setOwner(user);	
		
		return theOrder;
	}

}

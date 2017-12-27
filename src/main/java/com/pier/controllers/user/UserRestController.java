package com.pier.controllers.user;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.rest.model.PurchaseOrder;
import com.pier.security.JwtUser;
import com.pier.security.JwtUserFactory;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.impl.OrderService;
import com.pier.service.impl.UserService;

@RestController
public class UserRestController {
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private OrderService orderSvc;
	
	@Autowired
	UserService userSvc;
	
	@RequestMapping(value="/user", method=RequestMethod.GET)
	public ResponseEntity<?> getauthenticatedUser(HttpServletRequest request){
		JwtUser user=null;
		
			String token=request.getHeader(tokenHeader);
			user=userSvc.getJwtUserFromToken(token);		
		
		return new ResponseEntity<JwtUser>(user,HttpStatus.OK);
	}
	
	@RequestMapping(value="/history", method=RequestMethod.GET)
	public ResponseEntity<?> getMyHistory(@RequestParam(value="pageSize") Integer pageSize,@RequestParam(value="index") Integer index,  HttpServletRequest request){
		JwtUser user=null;
		List<PurchaseOrder> orders=Collections.emptyList();
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		// we call this method instead of retrieving the user's orders collection to seize the username already present in token claims
		orders=orderSvc.getOrderHistory(index,pageSize,username);
		
		
		return new ResponseEntity<List<PurchaseOrder>>(orders,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/history/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> getMyHistoryOrder(@PathVariable Long id,  HttpServletRequest request){
		JwtUser user=null;
		PurchaseOrder order=null;
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		// we call this method instead of retrieving the user's orders collection to seize the username already present in token claims
		order=orderSvc.getOrder(id, username);
				
		return new ResponseEntity<PurchaseOrder>(order,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="addToFavorites", method=RequestMethod.POST)
	public ResponseEntity<?> addToFavorites(@RequestBody Product product, HttpServletRequest request){
		Product addedProduct=null;
		
			String token=request.getHeader(tokenHeader);
			addedProduct=userSvc.addToFavorites(product,token);
		
		return new ResponseEntity<Product>(addedProduct,HttpStatus.OK);
	}
	
	@RequestMapping(value="removeFromFavorites", method=RequestMethod.POST)
	public ResponseEntity<?> removeFromFavorites(@RequestBody Product product, HttpServletRequest request){
		Product removedProduct=null;
		
			String token=request.getHeader(tokenHeader);
			removedProduct=userSvc.removeFromFavorites(product,token);
		
		return new ResponseEntity<Product>(removedProduct,HttpStatus.OK);
	}
	
	@RequestMapping(value="favorites", method=RequestMethod.GET)
	public ResponseEntity<?> getFavorites(HttpServletRequest request){
		Set<Product> results=new HashSet();
		
			String token=request.getHeader(tokenHeader);
			User user=userSvc.getUserFromTokenWithFavs(token);
			
			
			results=userSvc.getFavorites(user);		
		
		return new ResponseEntity<Set<Product>>(results,HttpStatus.OK);		
	}
	
	@RequestMapping(value="/user/info",method=RequestMethod.GET)
	public ResponseEntity<?> getUserInfo(HttpServletRequest request){		
		User user=null;
		
		
			String token=request.getHeader(tokenHeader);
			user=userSvc.getUserFromToken(token);
		
		
		return new ResponseEntity<User>(user, HttpStatus.OK);
		
	}
	
	@RequestMapping(value="/user/info",method=RequestMethod.PUT)
	public ResponseEntity<?> changeUserInfo(@RequestBody User user, HttpServletRequest request){		
		User currentUser=null;
		if(user.getPassword()==null || user.getPassword().isEmpty()){
			return new ResponseEntity<String>("error, must contain password",HttpStatus.CONFLICT);
		}
		
			String token=request.getHeader(tokenHeader);
			currentUser=userSvc.updateAddressAndPassword(user, token);				
		
		
		return new ResponseEntity<User>(currentUser, HttpStatus.OK);
		
	}
	

}

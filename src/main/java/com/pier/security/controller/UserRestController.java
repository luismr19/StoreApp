package com.pier.security.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
		try{
			String token=request.getHeader(tokenHeader);
			user=userSvc.getJwtUserFromToken(token);
		}catch(Exception e){
			return new ResponseEntity<String>("",HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<JwtUser>(user,HttpStatus.OK);
	}
	
	@RequestMapping(value="/history", method=RequestMethod.GET)
	public ResponseEntity<?> getMyHistory(HttpServletRequest request){
		JwtUser user=null;
		List<PurchaseOrder> orders=Collections.emptyList();
		try{
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		// we call this method instead of retrieving the user's orders collection to seize the username already present in token claims
		orders=orderSvc.getOrderHistory(username);
		}catch(Exception e){
			return new ResponseEntity<String>("",HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<PurchaseOrder>>(orders,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="addToFavorites", method=RequestMethod.POST)
	public ResponseEntity<?> addToFavorites(@RequestBody Product product, HttpServletRequest request){
		Product addedProduct=null;
		try{
			String token=request.getHeader(tokenHeader);
			addedProduct=userSvc.addToFavorites(product,token);
		}catch(Exception e){
			return new ResponseEntity<String>("error adding product",HttpStatus.CONFLICT);
		}
		return new ResponseEntity<Product>(addedProduct,HttpStatus.OK);
	}
	
	@RequestMapping(value="removeFromFavorites", method=RequestMethod.POST)
	public ResponseEntity<?> removeFromFavorites(@RequestBody Product product, HttpServletRequest request){
		Product removedProduct=null;
		try{
			String token=request.getHeader(tokenHeader);
			removedProduct=userSvc.removeFromFavorites(product,token);
		}catch(Exception e){
			return new ResponseEntity<String>("error removing product",HttpStatus.CONFLICT);
		}
		return new ResponseEntity<Product>(removedProduct,HttpStatus.OK);
	}
	
	@RequestMapping(value="favorites", method=RequestMethod.GET)
	public ResponseEntity<?> getFavorites(HttpServletRequest request){
		Set<Product> results=new HashSet();
		
		try{
			String token=request.getHeader(tokenHeader);
			User user=userSvc.getUserFromTokenWithFavs(token);
			
			
			results=userSvc.getFavorites(user);
		}catch(Exception e){
			return new ResponseEntity<String>("error getting favorites",HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<Set<Product>>(results,HttpStatus.OK);		
	}
	

}

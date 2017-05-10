package com.pier.controllers.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.pier.business.CartOperationsDelegate;
import com.pier.business.exception.OutOfStockException;
import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.UserDao;

@RestController
@EnableWebMvc
public class CartRestController {
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;		
	
	@Autowired
	CartOperationsDelegate cartOps;
	
	@Autowired
	UserDao userDao;
	
	
	@RequestMapping(value="addToCart",method=RequestMethod.POST)
	public ResponseEntity<String> addToCart(@RequestBody Product product,HttpServletRequest request){
			
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);		
				
			
		try{
			cartOps.addToCart(user,product);
		}catch(OutOfStockException ex){
		return new ResponseEntity<String>("out of stock",HttpStatus.OK);
		}
		catch(Exception e){
			return new ResponseEntity<String>("Error adding product",HttpStatus.CONFLICT);	
		}
		return new ResponseEntity<String>("successfully added to Cart",HttpStatus.OK);
		
	}
	
	
	
	@RequestMapping(value="removeFromCart",method=RequestMethod.GET)
	public ResponseEntity<String> removeFromCart(@RequestParam("id") Long id, HttpServletRequest request){
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
		
		try{
		cartOps.removeFromCart(user, id);
		}catch(Exception e){
			return new ResponseEntity<String>("unable to remove product",HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<String>("successfully removed",HttpStatus.GONE);		
		
	}	
	

}

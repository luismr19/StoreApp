package com.pier.controllers.user;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;

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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pier.business.CartOperationsDelegate;
import com.pier.business.exception.OutOfStockException;
import com.pier.model.security.User;
import com.pier.rest.model.Address;
import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.PurchaseOrder;
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
	public ResponseEntity<?> addToCart(@RequestBody ProductFlavor product, HttpServletRequest request){
			
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);		
				
		PurchaseOrder cart=null;
		try{
			cart= cartOps.addToCart(user,product);
		}catch(OutOfStockException ex){
		return new ResponseEntity<String>("out of stock",HttpStatus.OK);
		}
		
		return new ResponseEntity<PurchaseOrder>(cart,HttpStatus.OK);
		
	}
	
	
	@RequestMapping(value="addItemsToCart",method=RequestMethod.POST)
	public ResponseEntity<?> addToCart(@RequestBody ObjectNode json, HttpServletRequest request){
			
		ObjectMapper mapper=new ObjectMapper();
		
		ProductFlavor product;
		
		try {
			product = mapper.treeToValue(json.get("flavor"),ProductFlavor.class);
		} catch (JsonProcessingException e1) {
			return new ResponseEntity<String>("Error adding product",HttpStatus.BAD_REQUEST);
		}
		
		int howmany=json.get("amount").asInt();
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);		
		
		PurchaseOrder cart=null;
			
		try{
			cart=cartOps.addToCart(user,product,howmany);
		}catch(OutOfStockException ex){
		return new ResponseEntity<String>("out of stock",HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<PurchaseOrder>(cart,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="addPromotionCode",method=RequestMethod.POST)
	public ResponseEntity<?> addPromotionCode(@RequestBody ObjectNode json, HttpServletRequest request){
			
		ObjectMapper mapper=new ObjectMapper();
		
		String code=json.get("code").asText();
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);		
		
		PurchaseOrder cart=null;		
		
		cart=cartOps.addPromotionCodeToCart(user,code);		
		
		
		return new ResponseEntity<PurchaseOrder>(cart,HttpStatus.OK);
	}
	
	
	
	@RequestMapping(value="removeFromCart",method=RequestMethod.POST)
	public ResponseEntity<?> removeFromCart(@RequestBody ProductFlavor product, HttpServletRequest request){
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
		
		PurchaseOrder cart=null;		
		
			cart=cartOps.removeFromCart(user, product);		
		
		return new ResponseEntity<PurchaseOrder>(cart,HttpStatus.OK);			
	}
	
	@RequestMapping(value="updateQuants",method=RequestMethod.POST)
	public ResponseEntity<?> updateQuantities(@RequestBody List<OrderDetail> details, HttpServletRequest request){
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);
		
		PurchaseOrder cart=null;
		
		try{
			cart=cartOps.updateQuantities(user, details);
		}catch(OutOfStockException e){
			return new ResponseEntity<String>("One or more of the selected products are out of stock",HttpStatus.CONFLICT);
		}
		
		return new ResponseEntity<PurchaseOrder>(cart,HttpStatus.OK);	
		
	}
	
	@RequestMapping(value="cart",method=RequestMethod.GET)	
	public ResponseEntity<?> getUserCart(HttpServletRequest request){
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);
		User user=userDao.find("username",username).get(0);
		PurchaseOrder cart=new PurchaseOrder();
		
			cart=cartOps.getUserCart(user);	
		
		return new ResponseEntity<PurchaseOrder>(cart,HttpStatus.OK);
		
	}
	
	@RequestMapping(value="cart/promos",method=RequestMethod.POST)
	public ResponseEntity<?> getEligiblePromotions(@RequestBody PurchaseOrder cart){		
			return ResponseEntity.ok(cartOps.applyPromotionsReadOnly(cart));
			
	}
	
	@RequestMapping(value="cart/able",method=RequestMethod.GET)
	public ResponseEntity<?> isAbletoPurchase(HttpServletRequest request){		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);
		User user=userDao.find("username",username).get(0);
		PurchaseOrder cart=new PurchaseOrder();
		
			cart=cartOps.getUserCart(user);	
			
			List<String> outOfStockIndicators=cartOps.isOutOfStockForCart(cart);
			
			if(outOfStockIndicators==null){
				return new ResponseEntity<String>("cart is empty",HttpStatus.CONFLICT);
			}else if(outOfStockIndicators.size()>0){
				return new ResponseEntity<List<String>>(outOfStockIndicators,HttpStatus.CONFLICT);
			}
			
			return ResponseEntity.ok(cart);
	}
	

}

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

import com.pier.business.PaymentErrorException;
import com.pier.business.PaymentException;
import com.pier.business.PromotionsAppliance;
import com.pier.business.PurchaseOperationsDelegate;
import com.pier.business.exception.EmptyCartException;
import com.pier.business.exception.OutOfStockException;
import com.pier.model.security.User;
import com.pier.payment.request.PaymentEvent;
import com.pier.rest.model.Address;
import com.pier.rest.model.CheckoutRequest;
import com.pier.rest.model.PurchaseOrder;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.ProductDao;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

@RestController
@EnableWebMvc
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
	
	@Autowired
	PurchaseOperationsDelegate purchaseOps;
	
	@RequestMapping(value="checkout",method=RequestMethod.PUT)
	public ResponseEntity<?> checkout(@RequestBody CheckoutRequest checkoutInfo,
			HttpServletRequest request){		
		
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);			
		User user=userDao.find("username",username).get(0);	
		
		PurchaseOrder order=null;
		
		try{		
			order=purchaseOps.checkout(user,checkoutInfo);
		}catch(OutOfStockException os){
			return new ResponseEntity<String>(os.getMessage(),HttpStatus.CONFLICT);
		}catch(EmptyCartException ec){
			return new ResponseEntity<String>(ec.getMessage(),HttpStatus.CONFLICT);
		}catch(PaymentException pe){
			return new ResponseEntity<String>(pe.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}catch(PaymentErrorException perr){
			return new ResponseEntity<String>(perr.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
		}
		catch(Exception e){
			return new ResponseEntity<String>("error performing operation",HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<PurchaseOrder>(order,HttpStatus.OK);
		
		
	}
	
	@RequestMapping(value="notify",method=RequestMethod.GET)
	public ResponseEntity<?> notifyPaymentEvent(@RequestBody PaymentEvent event){		
		
		try {
			if(purchaseOps.handleNotification(event)){
				return ResponseEntity.ok("success");
			}
		} catch (Exception e) {
			return new ResponseEntity<String>("failure",HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<String>("failure",HttpStatus.NOT_FOUND);
		
		
	}	
	

}

package com.pier.controllers.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.rest.model.UserProductCall;
import com.pier.service.ProductDao;
import com.pier.service.impl.UserProductCallService;
import com.pier.service.impl.UserService;

@RestController
public class UserProductCallRestController {
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	UserProductCallService productCallSvc;
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	
	@RequestMapping(value = "wish", method = RequestMethod.POST)
	public ResponseEntity<?> newProductCall(@RequestParam(value = "id", required = true) Long id,
			HttpServletRequest request,UriComponentsBuilder ucBuilder) {
	
		String token=request.getHeader(tokenHeader);
		User caller=userSvc.getUserFromToken(token);
		Product calledProduct=productDao.find(id);
		
		if(calledProduct!=null){
		
		UserProductCall productCall=productCallSvc.newProductCall(calledProduct, caller);
		
		return new ResponseEntity<UserProductCall>(productCall, HttpStatus.CREATED);
		}else{
			return new ResponseEntity<String>("Product not found", HttpStatus.NOT_FOUND);		
		}
	
	}

}

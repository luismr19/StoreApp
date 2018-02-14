package com.pier.controllers.admin;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pier.model.security.User;
import com.pier.rest.model.Article;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductCallStatus;
import com.pier.rest.model.UserProductCall;
import com.pier.service.ProductDao;
import com.pier.service.UserProductCallDao;
import com.pier.service.impl.UserProductCallService;
import com.pier.service.impl.UserService;

@RestController
public class ManageUserProductCallsRestController {
	
	@Autowired
	private UserProductCallDao userProductCallDao;
	
	@Autowired
	private ProductDao productDao;
	
	@Autowired
	UserService userSvc;
	
	@Autowired
	UserProductCallService productCallSvc;
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	private Session currentSession() {
		return userProductCallDao.currentSession();
	}
		
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "productcall", method = RequestMethod.POST)
	public ResponseEntity<?> newProductCall(@RequestParam(value = "id", required = true) Long id,
			HttpServletRequest request,UriComponentsBuilder ucBuilder) {

		
		String token=request.getHeader(tokenHeader);
		User caller=userSvc.getUserFromToken(token);
		Product calledProduct=productDao.find(id);
		
		UserProductCall productCall=productCallSvc.newProductCall(calledProduct, caller);		
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/productcall/{id}").buildAndExpand(productCall.getId()).toUri());
		return new ResponseEntity<UserProductCall>(productCall, headers, HttpStatus.CREATED);
	
		
	}
	
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "productcall", method = RequestMethod.PUT)
	public ResponseEntity<?> updateProductCall(@RequestBody ObjectNode body) {
		
		Long id=body.get("id").asLong();
		String status=body.get("status").asText("DISABLED");
		
		UserProductCall productCall=productCallSvc.updateProductCall(id,status);

		if(productCall==null)
			return new ResponseEntity<String>("user product call not found", HttpStatus.NOT_FOUND);		
			
		return new ResponseEntity<UserProductCall>(productCall, HttpStatus.OK);
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "productcall", method = RequestMethod.GET)
	public ResponseEntity<?> find(@RequestParam(value = "index") int index, 
			@RequestParam(value="status",required=false)String status,
			@RequestParam(value="from",required=false)String from,
			@RequestParam(value="to",required=false)String to) {

			return ResponseEntity.ok(productCallSvc.findUserProductCalls(index, status, from, to));		
		 
	}
	
	

}

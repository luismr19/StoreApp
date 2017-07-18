package com.pier.controllers.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.ProductTypeIntegrityChecker;
import com.pier.rest.model.ProductType;
import com.pier.service.ProductTypeDao;

@RestController
@RequestMapping(value="productType")
@Transactional
public class ManageProductTypeRestController {
	
	@Autowired
	ProductTypeDao dao;
	
	@Autowired
	ProductTypeIntegrityChecker checker;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<ProductType> list(@RequestParam("index") int index){
		Criteria criteria = currentSession().createCriteria(ProductType.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(index).setMaxResults(50);
		return criteria.list();
	}	
	
	
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<?> getProductType(@PathVariable Long id){
		
		ProductType productType=dao.find(id);		
		if(productType!=null){
			return new ResponseEntity<ProductType>(productType,HttpStatus.OK);
		}		
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createProductType(@RequestBody ProductType productType,UriComponentsBuilder ucBuilder){
		if(checker.checkIfDuplicate(productType)){
			return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.CONFLICT);
		}
		dao.add(productType);
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/productType/{id}").buildAndExpand(productType.getId()).toUri());
        
		return new ResponseEntity<ProductType>(productType,headers,HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> updateProductType(@RequestBody ProductType productType, @PathVariable Long id){
		ProductType currentProductType=dao.find(id);		
		if(currentProductType!=null){
			currentProductType.setName(productType.getName());
			dao.update(currentProductType);
			return new ResponseEntity<ProductType>(currentProductType,HttpStatus.NOT_FOUND);
		}		
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="{id}",method=RequestMethod.DELETE)
	public ResponseEntity<?> removeProductType(@PathVariable Long id){
		ProductType productType=dao.find(id);		
		if(productType!=null){
			dao.removeProductType(productType);
			return new ResponseEntity<ProductType>(HttpStatus.NO_CONTENT);
		}		
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	

}

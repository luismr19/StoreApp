package com.pier.controllers.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pier.rest.model.Flavor;

@RestController
@RequestMapping(value="flavors")
@Transactional
public class ManageFlavorRestController {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<?> getFlavors(@RequestParam(value="index",required=false)Integer index){
		index=(index==null)?0:index;
		int pageSize=30;
		Criteria crit=currentSession().createCriteria(Flavor.class);
		crit.addOrder(Order.asc("id"));
		crit.setFirstResult(index).setMaxResults(pageSize);
		List<Flavor> results=crit.list();
		if(!results.isEmpty())
		return new ResponseEntity<List<Flavor>>(results,HttpStatus.OK);
		
		return new ResponseEntity(HttpStatus.NOT_FOUND); 
		
	}

}

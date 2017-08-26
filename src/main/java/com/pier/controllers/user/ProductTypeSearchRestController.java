package com.pier.controllers.user;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.pier.rest.model.ProductType;
import org.springframework.transaction.annotation.Transactional;

@RestController
@RequestMapping("searchProductType")
@Transactional
public class ProductTypeSearchRestController {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(params = {"index","word"},method=RequestMethod.GET)
	public ResponseEntity<List<ProductType>> filter(@RequestParam("index") int index,@RequestParam("word") String word){
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(ProductType.class);		
		criteria.add(Restrictions.ilike("name", "%"+word+"%"));		
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		List<ProductType> results = criteria.list();
		
		if (results.isEmpty()) {
			return new ResponseEntity<List<ProductType>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<ProductType>>(results, HttpStatus.OK);
		}
	}

}

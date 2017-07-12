package com.pier.controllers.user;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pier.rest.model.Product;


@RestController
@RequestMapping("search")
public class ProductSearchRestController {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(value="like",method=RequestMethod.GET)
	public ResponseEntity<List<Product>> searchProductLike(@RequestParam("word") String word){
		int pageSize=50;
		
		Criteria criteria = currentSession().createCriteria(Product.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.like("description", word));
		or.add(Restrictions.like("name", word));
		
		criteria.add(or);
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(0);
		criteria.setMaxResults(pageSize);
		List<Product> results=criteria.list();
		
		
		if(results.isEmpty()){
			return new ResponseEntity<List<Product>>(HttpStatus.NO_CONTENT);
		}else{
			return new ResponseEntity<List<Product>>(results,HttpStatus.OK);
		}
	}
	@RequestMapping(value="more",method=RequestMethod.GET)
	public ResponseEntity<List<Product>> getMoreResults(@RequestParam("word") String word,@RequestParam("index") int index){
		int pageSize=50;
		
		Criteria criteria = currentSession().createCriteria(Product.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.like("description", word));
		or.add(Restrictions.like("name", word));
		
		criteria.add(or);
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index);
		criteria.setMaxResults(pageSize);
		List<Product> results=criteria.list();
		
		
		if(results.isEmpty()){
			return new ResponseEntity<List<Product>>(HttpStatus.NO_CONTENT);
		}else{
			return new ResponseEntity<List<Product>>(results,HttpStatus.OK);
		}
	}
	
	@RequestMapping(value="advanced",method=RequestMethod.POST)
	public ResponseEntity<List<Product>> advancedSearch(@RequestBody AdvancedSearchRequest search, @RequestParam("index") int index){
		int pageSize=50;
		
		Criteria criteria = currentSession().createCriteria(Product.class);
		criteria.createAlias("brand", "br");
		criteria.createAlias("productType", "type");
		criteria.createAlias("categories", "cats");
		Disjunction or=Restrictions.disjunction();		
		
		or.add(Restrictions.eq("br.id", search.getBrandId()));
		or.add(Restrictions.eq("type.id", search.getProductTypeId()));
		or.add(Restrictions.in("cats.id", search.getCategoryIds()));
		criteria.add(or);
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index);
		criteria.setMaxResults(pageSize);
		
		List<Product> results=criteria.list();
		
		if(results.isEmpty()){
			return new ResponseEntity<List<Product>>(HttpStatus.NO_CONTENT);
		}else{
			return new ResponseEntity<List<Product>>(results,HttpStatus.OK);
		}
		
	}

}
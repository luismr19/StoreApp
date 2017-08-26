package com.pier.controllers.user;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.pier.rest.model.Product;


@RestController
@RequestMapping("search")
@Transactional
public class ProductSearchRestController {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(value="like",method=RequestMethod.GET)
	public ResponseEntity<List<Product>> searchProductLike(@RequestParam(value="word",required=false) String word,
			@RequestParam(value="name",required=false) String name){
		int pageSize=30;
		
		Criteria criteria = currentSession().createCriteria(Product.class);
		Disjunction or=Restrictions.disjunction();
		
		if(word!=null && !word.isEmpty()){
		or.add(Restrictions.ilike("description", "%"+word+"%"));
		or.add(Restrictions.ilike("name", "%"+word+"%"));
		}if(name!=null && !name.isEmpty()){
			or.add(Restrictions.ilike("name", "%"+name+"%"));	
		}
		
		criteria.setProjection(Projections.distinct(Projections.property("id")));
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
		int pageSize=30;
		
		Criteria criteria = currentSession().createCriteria(Product.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.ilike("description", "%"+word+"%"));
		or.add(Restrictions.ilike("name", "%"+word+"%"));
		
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
	public ResponseEntity<List<Product>> advancedSearch(@RequestBody AdvancedSearchRequest search, 
		@RequestParam(value="index",required=false) Integer index){
		index=(index==null)?0:index;
		int pageSize=9;
		
		Criteria criteria = currentSession().createCriteria(Product.class);
		criteria.createAlias("brand", "br");
		criteria.createAlias("productType", "type");
		criteria.createAlias("categories", "cats");
		Disjunction isPresentIn=Restrictions.disjunction();		
		
		if(search.getBrandIds()!=null && search.getBrandIds().size()>0)
		isPresentIn.add(Restrictions.in("br.id", search.getBrandIds()));
		
		if(search.getProductTypeIds()!=null && search.getProductTypeIds().size()>0)
		isPresentIn.add(Restrictions.in("type.id", search.getProductTypeIds()));
		
		if(search.getCategoryIds()!=null && search.getCategoryIds().size()>0)
		isPresentIn.add(Restrictions.in("cats.id", search.getCategoryIds()));
		
		if(!StringUtils.isEmpty(search.getName())){
		criteria.add(Restrictions.ilike("name", "%"+search.getName()+"%"));
		criteria.add(Restrictions.and(isPresentIn));
		}else{
			criteria.add(isPresentIn);	
		}
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

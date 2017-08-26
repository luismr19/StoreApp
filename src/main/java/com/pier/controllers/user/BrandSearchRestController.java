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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;
import com.pier.rest.model.Brand;


@RestController
@RequestMapping("searchBrand")
@Transactional
public class BrandSearchRestController {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	
	@RequestMapping(value="like",method=RequestMethod.GET)
	public ResponseEntity<List<Brand>> getMoreResults(@RequestParam("name") String word,
			@RequestParam(value="index", required=false) Integer index){
		index=(index==null)?0:index;
		int pageSize=30;
		
		Criteria criteria = currentSession().createCriteria(Brand.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.ilike("shortName", "%"+word+"%"));
		or.add(Restrictions.ilike("name", "%"+word+"%"));
		
		criteria.add(or);
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index);
		criteria.setMaxResults(pageSize);
		List<Brand> results=criteria.list();
		
		
		if(results.isEmpty()){
			return new ResponseEntity<List<Brand>>(HttpStatus.NO_CONTENT);
		}else{
			return new ResponseEntity<List<Brand>>(results,HttpStatus.OK);
		}
	}

}

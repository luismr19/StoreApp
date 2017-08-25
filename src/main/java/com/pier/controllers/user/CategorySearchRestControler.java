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

import com.pier.rest.model.Category;

@RestController
@RequestMapping("searchCat")
public class CategorySearchRestControler {
	@Autowired
	private SessionFactory sessionFactory;

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	@RequestMapping(method=RequestMethod.GET)
	public ResponseEntity<List<Category>> getMoreResults(@RequestParam("word") String word,
			@RequestParam(value="index",required=false) Integer index) {
		index=(index==null)?0:index;
		int pageSize = 30;		

		Criteria criteria = currentSession().createCriteria(Category.class);

		criteria.add(Restrictions.like("name", "%"+word+"%"));
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index);
		criteria.setMaxResults(pageSize);
		List<Category> results = criteria.list();

		if (results.isEmpty()) {
			return new ResponseEntity<List<Category>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Category>>(results, HttpStatus.OK);
		}
	}

}

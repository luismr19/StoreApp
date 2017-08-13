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

	@RequestMapping(value = "like", method = RequestMethod.GET)
	public ResponseEntity<List<Category>> searchBrandLike(@RequestParam("word") String word) {
		int pageSize = 30;

		Criteria criteria = currentSession().createCriteria(Category.class);

		criteria.add(Restrictions.like("name", "%"+word+"%"));
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(0);
		criteria.setMaxResults(pageSize);
		List<Category> results = criteria.list();

		if (results.isEmpty()) {
			return new ResponseEntity<List<Category>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Category>>(results, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "more", method = RequestMethod.GET)
	public ResponseEntity<List<Category>> getMoreResults(@RequestParam("word") String word,
			@RequestParam("index") int index) {
		int pageSize = 30;

		Criteria criteria = currentSession().createCriteria(Category.class);

		criteria.add(Restrictions.like("name", "%"+word+"%"));
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(0);
		criteria.setMaxResults(pageSize);
		List<Category> results = criteria.list();

		if (results.isEmpty()) {
			return new ResponseEntity<List<Category>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Category>>(results, HttpStatus.OK);
		}
	}

}

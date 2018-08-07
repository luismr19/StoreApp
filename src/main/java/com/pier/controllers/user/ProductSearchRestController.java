package com.pier.controllers.user;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import org.apache.commons.lang3.text.StrBuilder;
import org.hibernate.Criteria;
import org.hibernate.query.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.Subqueries;
import org.hibernate.transform.AliasToBeanResultTransformer;
import org.hibernate.transform.Transformers;
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

import com.pier.rest.model.Category;
import com.pier.rest.model.Product;
import com.pier.service.ProductDao;

@RestController
@RequestMapping("search")
@Transactional
public class ProductSearchRestController {

	@Autowired
	private SessionFactory sessionFactory;

	@Autowired
	ProductDao dao;

	private Session currentSession() {
		return sessionFactory.getCurrentSession();
	}

	@RequestMapping(value = "like", method = RequestMethod.GET)
	public ResponseEntity<List<Product>> searchProductLike(@RequestParam(value = "word", required = false) String word,
			@RequestParam(value = "name", required = false) String name, @RequestParam("index") Integer index) {
		int pageSize = 30;
		index = (index != null) ? index : 0;

		Criteria criteria = currentSession().createCriteria(Product.class);
		Disjunction or = Restrictions.disjunction();

		if (word != null && !word.isEmpty()) {
			or.add(Restrictions.ilike("description", "%" + word + "%"));
			or.add(Restrictions.ilike("name", "%" + word + "%"));
		}
		if (name != null && !name.isEmpty()) {
			or.add(Restrictions.ilike("name", "%" + name + "%"));
		}

		criteria.add(or);
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index);
		criteria.setMaxResults(pageSize);

		List<Product> results = criteria.list();

		if (results.isEmpty()) {
			return new ResponseEntity<List<Product>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Product>>(results, HttpStatus.OK);
		}
	}

	@RequestMapping(value = "any", method = RequestMethod.GET)
	public ResponseEntity<List<Product>> getAnyProducts(@RequestParam(value = "type", required = false) Long type,
			@RequestParam(value = "cat", required = false) Long cat, @RequestParam("number") Integer number) {

		if (number > 10)
			number = 10;

		Object result = null; // will later contain a random entity
		Criteria criteria = currentSession().createCriteria(Product.class);
		
		Criterion enabled=Restrictions.eq("enabled", true);
		
		Criterion typeRestriction = Restrictions.eq("productType.id", type);

		if (cat != null) {
			DetachedCriteria categoryCriteria = DetachedCriteria.forClass(Category.class);
			categoryCriteria.add(Restrictions.eq("id", cat));

			categoryCriteria.setProjection(Projections.property("id"));

			criteria.add(Subqueries.propertyIn("id", categoryCriteria));
		}

		if (type != null)
			criteria.add(typeRestriction);
		
		criteria.setProjection(Projections.property("id"));

		List<Long> ids = criteria.list();

		List<Product> results = new ArrayList<>();

		if (ids.size() > 0) {

			int index = 0;
			for (int i = 0; i < number; i++) {
				index = new Random().nextInt(ids.size());
				results.add(dao.find(ids.get(index)));
			}

		}

		return new ResponseEntity<List<Product>>(results, HttpStatus.OK);

	}

	@RequestMapping(value = "advanced", method = RequestMethod.POST)
	public ResponseEntity<List<Product>> advancedSearch(@RequestBody AdvancedSearchRequest search,
			@RequestParam(value = "index", required = false) Integer index) {
		index = (index == null) ? 0 : index;
		int pageSize = 9;

		List<Long> typesIds = search.getProductTypeIds();
		List<Long> brandsIds = search.getBrandIds();
		List<Long> catsIds = search.getCategoryIds();

		if (typesIds.isEmpty())
			typesIds = Arrays.asList(0L);

		if (brandsIds.isEmpty())
			brandsIds = Arrays.asList(0L);

		if (catsIds.isEmpty())
			catsIds = Arrays.asList(0L);

		String mainQuery = "select distinct prod from Product prod left join prod.categories cats where prod.productType.id in (:productTypes) or "
				+ "prod.brand.id in (:brands) or cats in (select cat from Category cat where cat.id in (:catsIds)) order by prod.name";

		Query findProducts = currentSession().createQuery(mainQuery);

		findProducts.setParameterList("productTypes", typesIds);
		findProducts.setParameterList("brands", brandsIds);
		findProducts.setParameterList("catsIds", catsIds);

		findProducts.setMaxResults(pageSize);
		findProducts.setFirstResult(index);

		List<Product> results = findProducts.list();

		if (results.isEmpty()) {
			return new ResponseEntity<List<Product>>(HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<List<Product>>(results, HttpStatus.OK);
		}

	}

}

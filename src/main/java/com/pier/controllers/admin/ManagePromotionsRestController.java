package com.pier.controllers.admin;

import java.util.List;
import java.util.Optional;

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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.PromotionIntegrityChecker;
import com.pier.rest.model.Promotion;
import com.pier.service.PromotionDao;


@RestController
@RequestMapping(value = "promotions")
public class ManagePromotionsRestController {

	@Autowired
	PromotionDao dao;

	@Autowired
	PromotionIntegrityChecker checker;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Promotion> list(@RequestParam("index") int index){
		Criteria criteria = currentSession().createCriteria(Promotion.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(index).setMaxResults(50);
		return criteria.list();
	}
	@RequestMapping(params = {"word","index"},method=RequestMethod.GET)
	public List<Promotion> filter(@RequestParam("index") int index,@RequestParam("filter") String word,
			@RequestParam(value = "featured", defaultValue="n",required=false)String featured){
		Criteria criteria = currentSession().createCriteria(Promotion.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.like("displayName", "%"+word+"%"));
		or.add(Restrictions.like("description", "%"+word+"%"));		
		criteria.add(or);
        if(featured=="y"){
        	criteria.add(Restrictions.and(Restrictions.eq("featured", true)));
		}
		criteria.addOrder(Order.asc("displayName"));
		criteria.setFirstResult(index).setMaxResults(50);
		return criteria.list();		
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPromotion(@PathVariable Long id) {
		Promotion promo = dao.find(id);
		if (promo != null) {
			return new ResponseEntity<Promotion>(promo, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createPromotion(@RequestBody Promotion promo, UriComponentsBuilder ucBuilder) {
		if (!checker.checkIfDuplicate(promo) && checker.checkIfValid(promo)) {
			dao.add(promo);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/articles/{id}").buildAndExpand(promo.getId()).toUri());
			return new ResponseEntity<Promotion>(promo, headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.PUT, value = "{id}")
	public ResponseEntity<?> updatePromotion(@PathVariable Long id, @RequestBody Promotion promo) {
		Promotion currentPromo = dao.find(id);
		if (currentPromo != null) {
			if (checker.checkIfValid(promo)) {
				currentPromo.setDescription(promo.getDescription());
				currentPromo.setDisplayName(promo.getDisplayName());
				currentPromo.setEnabled(promo.getEnabled());
				currentPromo.setEndDate(promo.getEndDate());
				currentPromo.setStartDate(promo.getStartDate());
				currentPromo.setInclusive(promo.getInclusive());
				currentPromo.setPromotionrule(promo.getPromotionRule());

				dao.update(currentPromo);
				return new ResponseEntity<Promotion>(currentPromo, HttpStatus.FOUND);
			}
			return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Promotion> delete(@PathVariable Long id) {
		Promotion promo = dao.find(id);

		if (promo != null) {
			dao.removePromotion(promo);
			return new ResponseEntity<Promotion>(promo, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Promotion>(HttpStatus.NOT_FOUND);

	}
}

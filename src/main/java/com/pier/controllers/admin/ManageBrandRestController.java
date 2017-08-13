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

import com.pier.business.validation.BrandIntegrityChecker;
import com.pier.rest.model.Brand;
import com.pier.service.BrandDao;

@RestController
@RequestMapping(value="brands")
@Transactional
public class ManageBrandRestController {
	
	@Autowired
	BrandDao brandDao;
	
	@Autowired
	BrandIntegrityChecker checker;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Brand>getBrands(@RequestParam("index") int index){
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(Brand.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();
	}
	
	@RequestMapping(params = {"index","word"},method=RequestMethod.GET)
	public List<Brand> filter(@RequestParam("index") int index,@RequestParam("filter") String word){
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(Brand.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.like("shortName", "%"+word+"%"));
		or.add(Restrictions.like("name", "%"+word+"%"));
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();		
	}
	
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> getBrand(@PathVariable long id){
		Brand brand=brandDao.find(id);
		if(brand!=null){
			return new ResponseEntity<Brand>(brand,HttpStatus.FOUND);
		}
		
		return new ResponseEntity<Brand>(brand,HttpStatus.NOT_FOUND);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createBrand(@RequestBody Brand brand, UriComponentsBuilder ucBuilder){

		// check if exists
		if(!checker.checkIfDuplicate(brand)){
			brandDao.add(brand);
			
			HttpHeaders headers = new HttpHeaders();
	        headers.setLocation(ucBuilder.path("/brands/{id}").buildAndExpand(brand.getId()).toUri());
	        
			return new ResponseEntity<Brand>(brand,headers,HttpStatus.CREATED);
		}
		return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.CONFLICT);
		}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateBrand(@PathVariable long id,@RequestBody Brand brand){

		Brand currentBrand=brandDao.find(id);
		// check if exists
		if(currentBrand!=null){
			currentBrand.setName(brand.getName());
			currentBrand.setShortName(brand.getShortName());
			
			brandDao.update(currentBrand);
			return new ResponseEntity<Brand> (currentBrand, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/{id}",method=RequestMethod.DELETE)
	public ResponseEntity<Brand> deleteBrand(@PathVariable long id){
		Brand brand=brandDao.find(id);
		if(brand==null){
			return new ResponseEntity<Brand>(HttpStatus.NOT_FOUND);
		}
		brandDao.removeBrand(brand);
		return new ResponseEntity<Brand>(HttpStatus.NO_CONTENT);
	}
	
	}



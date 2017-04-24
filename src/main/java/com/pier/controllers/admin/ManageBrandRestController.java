package com.pier.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.BrandIntegrityChecker;
import com.pier.rest.model.Brand;
import com.pier.service.BrandDao;

@RestController
@RequestMapping(value="brands")
public class ManageBrandRestController {
	
	@Autowired
	BrandDao brandDao;
	
	@Autowired
	BrandIntegrityChecker checker;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Brand>getBrands(){
		return brandDao.list();
	}
	
	@RequestMapping(value="/{id}", method=RequestMethod.GET)
	public ResponseEntity<?> getBrand(@PathVariable long id){
		Brand brand=brandDao.find(id);
		if(brand!=null){
			return new ResponseEntity<Brand>(brand,HttpStatus.FOUND);
		}
		
		return new ResponseEntity<Brand>(brand,HttpStatus.NOT_FOUND);
	}
	
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
	
	@RequestMapping(value="/{id}", method=RequestMethod.PUT)
	public ResponseEntity<?> updateBrand(@PathVariable long id,@RequestBody Brand brand){

		Brand currentBrand=brandDao.find(id);
		// check if exists
		if(currentBrand!=null){
			currentBrand.setFullName(brand.getFullName());
			currentBrand.setShortName(brand.getShortName());
			
			brandDao.update(currentBrand);
			return new ResponseEntity<Brand> (currentBrand, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
	
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



package com.pier.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.CategoryIntegrityChecker;
import com.pier.rest.model.Category;
import com.pier.service.CategoryDao;

@RestController
@RequestMapping(value="categories")
public class ManageCategoryRestController {
	
	@Autowired
	CategoryDao dao;
	
	@Autowired
	CategoryIntegrityChecker checker;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Category> list(){
		return dao.list();
	}
	
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<?> getCategory(@PathVariable Long id){
		
		Category category=dao.find(id);		
		if(category!=null){
			return new ResponseEntity<Category>(category,HttpStatus.OK);
		}		
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createCategory(@RequestBody Category category,UriComponentsBuilder ucBuilder){
		if(checker.checkIfDuplicate(category)){
			return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.CONFLICT);
		}
		dao.add(category);
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/categories/{id}").buildAndExpand(category.getId()).toUri());
        
		return new ResponseEntity<Category>(category,headers,HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> updateCategory(@RequestBody Category category, @PathVariable Long id){
		Category currentCategory=dao.find(id);		
		if(currentCategory!=null){
			currentCategory.setName(category.getName());
			dao.update(currentCategory);
			return new ResponseEntity<Category>(currentCategory,HttpStatus.NOT_FOUND);
		}		
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="{id}",method=RequestMethod.DELETE)
	public ResponseEntity<?> removeCategory(@PathVariable Long id){
		Category category=dao.find(id);		
		if(category!=null){
			dao.removeCategory(category);
			return new ResponseEntity<Category>(HttpStatus.NO_CONTENT);
		}		
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	

}

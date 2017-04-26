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

import com.pier.business.validation.ProductIntegrityChecker;
import com.pier.rest.model.Product;
import com.pier.service.ProductDao;

@RestController
@RequestMapping(value="products")
public class ManageProductsRestController {
	
	@Autowired
	ProductDao dao;
	
	@Autowired
	ProductIntegrityChecker checker;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Product> list(){
		return dao.list();
	}
	
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<Product> getProduct(@PathVariable Long id){
		
		Product product=dao.find(id);
		if(product!=null){
			return new ResponseEntity<Product>(product,HttpStatus.OK);
		}
		
		return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createProduct(@RequestBody Product product, UriComponentsBuilder ucBuilder){
		if(checker.checkIfDuplicate(product) || !checker.checkIfValid(product)){
			return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.CONFLICT);
		}
		dao.add(product);
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri());
        
		return new ResponseEntity<Product>(product,headers,HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product){
		Product currentProduct=dao.find(id);
		if(currentProduct==null){
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}
		if(checker.checkIfValid(product)){
						
			if(product.getBrand()!=null){
			currentProduct.setBrand(product.getBrand());
			}
			if(product.getProductType()!=null){
				currentProduct.setProductType(product.getProductType());
			}
			if(product.getCategories()!=null){
				currentProduct.setCategories(product.getCategories());
			}
			if(product.getProductType()!=null){
					currentProduct.setProductType(product.getProductType());
			}
			
			
			currentProduct.setName(product.getName());
			currentProduct.setDescription(product.getDescription());
			currentProduct.setEnabled(true);
			currentProduct.setPrice(product.getPrice());
			currentProduct.setExistence(product.getExistence());
			dao.update(product);
			
			return new ResponseEntity<Product>(product,HttpStatus.OK);			
		}
		
		return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.NO_CONTENT);        
		
	}
	

}

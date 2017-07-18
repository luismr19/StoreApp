package com.pier.controllers.user;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.transaction.annotation.Transactional;

import com.pier.rest.model.ProductFlavor;
import com.pier.service.impl.FlavorService;

@RestController
@RequestMapping("searchProductFlavor")
@Transactional
public class ProductFlavorSearchRestController {
	
	@Autowired
	private FlavorService flavorSvc;
	
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<?> getProductFlavors(@PathVariable Long id){
		List<ProductFlavor> results=flavorSvc.findProductFlavor(id);
		
		if(results.isEmpty()){
			return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
		}
		
		return new ResponseEntity<List<ProductFlavor>>(results,HttpStatus.OK);
		
	}
	
	

}

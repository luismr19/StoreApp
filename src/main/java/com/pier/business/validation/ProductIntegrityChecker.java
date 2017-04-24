package com.pier.business.validation;

import org.springframework.stereotype.Component;

import com.pier.rest.model.Product;
import com.pier.service.ProductDao;

@Component
public class ProductIntegrityChecker extends IntegrityCheckerImpl<Product,ProductDao> {
	
		
	@Override
	public boolean checkIfDuplicate(Product product){
		errors.clear();
		boolean result=dao.find("name", product.getName()).size()>0;
		 result=result && (dao.find("description", product.getDescription()).size()>0);
		if(result){
			errors.add("name and description combination already exists, choose different name or description");
		}
		return result;
	}

}

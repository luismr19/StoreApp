package com.pier.business.validation;

import org.springframework.stereotype.Component;

import com.pier.rest.model.ProductType;
import com.pier.service.ProductTypeDao;

@Component
public class ProductTypeIntegrityChecker extends IntegrityCheckerImpl<ProductType,ProductTypeDao> {

	@Override
	public boolean checkIfDuplicate(ProductType model) {
		errors.clear();
		boolean result=dao.find("name", model.getName()).size()>0;
		if(result){
			errors.add("Name already exists, use a different name");
		}
		return result;
	}

	
	
}

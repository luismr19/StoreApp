package com.pier.business.validation;

import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.pier.rest.model.Brand;
import com.pier.service.BrandDao;

@Component()
@Scope("prototype")
public class BrandIntegrityChecker extends IntegrityCheckerImpl<Brand, BrandDao>{

	@Override
	public boolean checkIfDuplicate(Brand model) {
		errors.clear();
		boolean result;
		result=dao.find("shortName", model.getShortName()).size()>0;
		result=result || dao.find("name", model.getName()).size()>0;
		if(!result){
			errors.add("Brand name already exists");
		}
		return result;
	}
	
	

}

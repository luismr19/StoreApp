package com.pier.business.validation;

import org.springframework.stereotype.Component;

import com.pier.rest.model.Flavor;
import com.pier.service.FlavorDao;

@Component
public class FlavorIntegrityChecker extends IntegrityCheckerImpl<Flavor, FlavorDao> {

	@Override
	public boolean checkIfDuplicate(Flavor model) {
		errors.clear();
		boolean result=dao.find("description", model.getFlavorName()).size()>0;
		if(result)
			errors.add("Flavor already exists, choose a different name");
		return result;
	}
	
	

}

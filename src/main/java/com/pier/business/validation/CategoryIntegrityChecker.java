package com.pier.business.validation;

import org.springframework.stereotype.Component;

import com.pier.rest.model.Category;
import com.pier.service.CategoryDao;

@Component
public class CategoryIntegrityChecker extends IntegrityCheckerImpl<Category,CategoryDao>{
	
	@Override
	public boolean checkIfDuplicate(Category model){
		boolean result;
		errors.clear();
		result=dao.find("name", model.getName()).size()>0;		
		if(result){
			errors.add("Category name already exists");
		}
		return result;
	}
	
	

}

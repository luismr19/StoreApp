package com.pier.service;

import com.pier.rest.model.Category;

public interface CategoryDao extends GenericDao<Category,Long>{
	
	boolean removeCategory(Category category);

}

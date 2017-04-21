package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Category;
import com.pier.service.CategoryDao;

@Repository("categoryDao")
public class CategoryDaoImpl extends HibernateDao<Category,Long> implements CategoryDao{

	@Override
	public boolean removeCategory(Category category) {
		delete(category);
		
		return true;
	}

}

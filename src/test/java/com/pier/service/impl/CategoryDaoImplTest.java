package com.pier.service.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pier.DomainAwareBase;
import com.pier.rest.model.Category;
import com.pier.service.CategoryDao;

public class CategoryDaoImplTest extends DomainAwareBase {

	@Autowired
	CategoryDao categoryDao;
	
	
	@Test
	public void testRemoveCategory() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		Category category1=new Category("muscle growth");		
		categoryDao.add(category1);
		
		assertTrue(categoryDao.find(category1.getId())!=null);
	}

	@Test
	public void testUpdate() {
		Category category=new Category("muscle growth");
		String name="siempre no";
		
		categoryDao.add(category);
		category.setName("siempre no");
		
		assertTrue(category.getName().equals(name));
	}

	@Test
	public void testList() {
		int initial =categoryDao.list().size();
		List<Category> categories=Arrays.asList(
				new Category("performance"),
				new Category("vitamin"));
		
		for(Category cat:categories){
			categoryDao.add(cat);
		}
		
		assertTrue(categoryDao.list().size()>initial);
	}	

	@Test
	public void testFindStringString() {
		Category category=new Category("fat loss");
		categoryDao.add(category);
		
		assertTrue(categoryDao.find("name", "fat loss").size()>0);
	}

}

package com.pier.service.impl;

import static org.junit.Assert.*;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import com.pier.DomainAwareBase;
import com.pier.rest.model.Brand;
import com.pier.rest.model.Category;
import com.pier.rest.model.Flavor;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductType;
import com.pier.service.BrandDao;
import com.pier.service.CategoryDao;
import com.pier.service.FlavorDao;
import com.pier.service.ProductDao;
import com.pier.service.ProductTypeDao;

public class ProductDaoImplTest extends DomainAwareBase {

	@Autowired
	ProductDao dao;
	
	@Autowired
	CategoryDao catDao;
	
	@Autowired
	ProductTypeDao typeDao;
	
	@Autowired
	BrandDao brandDao;
	
	@Autowired
	FlavorService flavorService;
	
	
	@Test
	public void testAdd() {
		List<Category> categories=Arrays.asList(new Category("muscle swolyness"),
				new Category("muscle repair"));
		Product product=new Product(new Brand("MP","Muscle Pharm"), new BigDecimal("70.50"), "someProduct", 
				"this will make you fly", categories,
				new ProductType("protein"), 2L, true);
		Flavor flavor=new Flavor("strawberry",5L);	
		flavor=flavorService.generateFlavor(flavor.getFlavorName(), flavor.getExistence());
		List<Flavor> flavors=flavorService.persistFlavors(flavor);
		
		product.setFlavors(flavors);		
		dao.add(product);
		Category theCategory=catDao.find(categories.get(0).getId());
		ProductType theType=typeDao.find(product.getProductType().getId());
		Brand theBrand=brandDao.find(product.getBrand().getId());
		//assert that the brand and everything was persisted along with the product
		Product persistedProduct=dao.find(product.getId());
		assertTrue(persistedProduct!=null && theCategory!=null && theType!=null && theBrand!=null);
	}

	@Test
	public void testUpdate() {
		
		List<Category> categories=Arrays.asList(new Category("muscle swolyness"),
				new Category("muscle repair"));
		Product product=new Product(new Brand("MP","Muscle Pharm"), new BigDecimal("70.50"), "someProduct", 
				"this will make you fly", categories,
				new ProductType("protein"), 2L, true);
		
		dao.add(product);
		String desiredDescription="joke, it will make you go to the restroom";
		product.setDescription(desiredDescription);
		
		dao.update(product);
		
		assertEquals(desiredDescription,dao.find(product.getId()).getDescription());
	}

	@Test
	public void testList() {
		List<Category> categories=Arrays.asList(new Category("muscle swolyness"),
				new Category("muscle repair"));
		Product product=new Product(new Brand("MP","Muscle Pharm"), new BigDecimal("70.50"), "someProduct", 
				"this will make you fly", categories,
				new ProductType("protein"), 2L, true);
		dao.add(product);
		assertTrue(dao.list().size()>0);
	}

	@Test
	public void testFindStringString() {
		List<Category> categories=Arrays.asList(new Category("muscle swolyness"),
				new Category("muscle repair"));
		Product product=new Product(new Brand("MP","Muscle Pharm"), new BigDecimal("70.50"), "someProduct", 
				"this will make you fly", categories,
				new ProductType("protein"), 2L, true);
		dao.add(product);
		
		assertTrue(dao.find("name","someProduct").size()>0);
	}
	
	

}

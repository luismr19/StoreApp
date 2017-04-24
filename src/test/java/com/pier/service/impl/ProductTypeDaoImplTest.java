package com.pier.service.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.pier.DomainAwareBase;
import com.pier.rest.model.ProductType;
import com.pier.service.ProductTypeDao;

public class ProductTypeDaoImplTest extends DomainAwareBase {
	
	@Autowired
	ProductTypeDao dao;	

	@Test
	public void testRemoveProductType() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		ProductType productType=new ProductType("protein");
		dao.add(productType);
		
		assertEquals(dao.find(productType.getId()).getName(),"protein");
	}

	@Test
	public void testUpdate() {
		String initial_name="aminoacid";
		ProductType productType=new ProductType("aminoacid");
		dao.add(productType);
		
		productType.setName("branched chain aminacid");
		dao.update(productType);
		
		assertNotEquals(dao.find(productType.getId()).getName(),initial_name);
	}

	@Test
	public void testList() {		
		
		ProductType productType=new ProductType("aminoacid");
		dao.add(productType);
		ProductType productType2=new ProductType("protein");
		dao.add(productType2);
		
		assertTrue(dao.list().size()>0);		
	}
	

	@Test
	public void testFindStringString() {
		List<ProductType> productTypes=Arrays.asList(
				new ProductType("creatine")
				,new ProductType("protein")
				,new ProductType("snack bar"));
		
		for(ProductType type:productTypes)
			dao.add(type);
		
		
		assertTrue(dao.find("name", "snack bar").size()>0);
	}

}

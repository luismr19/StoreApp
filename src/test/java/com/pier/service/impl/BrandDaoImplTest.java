package com.pier.service.impl;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import com.pier.DomainAwareBase;
import com.pier.rest.model.Brand;
import com.pier.rest.model.Flavor;
import com.pier.rest.model.Product;
import com.pier.service.BrandDao;
import com.pier.service.ProductDao;

public class BrandDaoImplTest extends DomainAwareBase {
	
	@Autowired
	BrandDao brandDao;
	
	@Autowired
	ProductDao productDao;
	
	static Brand muscleTech;
	static Brand on;
	
	@BeforeClass
	public static void createBrand(){
		muscleTech=new Brand("MuscleTech","MuscleTech");
		on=new Brand("O N","Optimum Nutrition");
	}

	@Test
	public void testRemoveBrand() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		Product product1=new Product();
		Product product2=new Product();
		
		product1.setFlavors(Arrays.asList(new Flavor("Banana with poop",5L)));
		product1.setEnabled(true);
		product1.setName("nitrotech");
		product1.setDescription("whey protein isolate");
		product1.setPrice(new BigDecimal("100.00"));
		
		product2.setFlavors(Arrays.asList(new Flavor("strawberry",5L)));
		product2.setEnabled(true);
		product2.setName("mass-tech");
		product2.setDescription("muscleTech - mass gainer");
		product2.setPrice(new BigDecimal("170.70"));
		
		
		
		Set<Product> products= new HashSet<Product>(Arrays.asList(product1,product2));
		
		product1.setBrand(muscleTech);
		product2.setBrand(muscleTech);
		muscleTech.setProducts(products);		
		brandDao.add(muscleTech);
		
		assertTrue(brandDao.find(muscleTech.getId())!=null);
	}

	@Test
	public void testUpdate() {
		
		Brand muscleTech2=new Brand("M T","muscleTech2");
		brandDao.add(muscleTech2);
		muscleTech2.setFullName("1Muscletech");		
		brandDao.update(muscleTech2);
		
		assertTrue(brandDao.find(muscleTech2.getId()).getFullName().equals("1Muscletech"));
	}

	@Test
	public void testList() {
		List<Brand> brands=Arrays.asList(
				new Brand("Universal","Universal Nutrition"),
				new Brand("Dymatize","Dymatize"));
		
		for(Brand brand: brands){
			brandDao.add(brand);
		}
		List<Brand> newBrands=brandDao.list();
		assertTrue(newBrands.size()>0);
	}

	@Test
	public void testFindStringString() {
		brandDao.add(on);
		assertTrue(brandDao.find("shortName", "O N").size()>0);
	}

}

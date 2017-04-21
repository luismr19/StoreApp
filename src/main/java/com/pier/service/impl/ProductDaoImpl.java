package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Product;
import com.pier.service.ProductDao;

@Repository("productDao")
public class ProductDaoImpl extends HibernateDao<Product, Long> implements ProductDao {

	
	@Override
	public boolean removeProduct(Product product) {
		delete(product);
		
		return true;
	}

}

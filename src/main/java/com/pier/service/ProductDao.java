package com.pier.service;

import com.pier.rest.model.Product;

public interface ProductDao extends GenericDao<Product, Long> {
	
	boolean removeProduct(Product product);

}

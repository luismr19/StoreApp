package com.pier.service.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.ProductFlavorId;
import com.pier.service.ProductFlavorDao;

@Repository("productFlavorDao")
public class ProductFlavorDaoImpl extends HibernateDao<ProductFlavor, ProductFlavorId> implements ProductFlavorDao {

	@Override
	public boolean removeProduct(ProductFlavor product) {
		delete(product);
		return true;
	}
	

}

package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.ProductType;
import com.pier.service.ProductTypeDao;

@Repository("productTypeDao")
public class ProductTypeDaoImpl extends HibernateDao<ProductType,Long> implements ProductTypeDao{

	@Override
	public boolean removeProductType(ProductType productType) {
		delete(productType);
		return true;
	}

}

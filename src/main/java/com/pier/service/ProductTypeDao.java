package com.pier.service;

import com.pier.rest.model.ProductType;

public interface ProductTypeDao extends GenericDao<ProductType,Long> {
	
	boolean removeProductType(ProductType productType);

}

package com.pier.service;

import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.ProductFlavorId;

public interface ProductFlavorDao extends GenericDao<ProductFlavor, ProductFlavorId> {
	
	boolean removeProduct(ProductFlavor product);

}

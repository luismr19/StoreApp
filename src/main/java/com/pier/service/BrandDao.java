package com.pier.service;

import com.pier.rest.model.Brand;

public interface BrandDao extends GenericDao<Brand, Long> {
	
	Boolean removeBrand(Brand brand);

}

package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Brand;
import com.pier.service.BrandDao;

@Repository("brandDao")
public class BrandDaoImpl extends HibernateDao<Brand,Long> implements BrandDao {

	@Override
	public Boolean removeBrand(Brand brand) {
		delete(brand);
		
		return true;
	}

}

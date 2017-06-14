package com.pier.service.impl;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Flavor;
import com.pier.service.FlavorDao;

@Repository("flavorDao")
public class FlavorDaoImpl extends HibernateDao<Flavor, Long> implements FlavorDao {


	@Override
	public boolean removeFlavor(Flavor flavor) {
		delete(flavor);
		return true;
	}

}

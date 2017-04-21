package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Benefit;
import com.pier.service.BenefitDao;

@Repository("benefitDao")
public class BenefitDaoImpl extends HibernateDao<Benefit,Long> implements BenefitDao {

	@Override
	public Boolean removeBenefit(Benefit benefit) {
		delete(benefit);
		return true;
	}

}

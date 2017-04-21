package com.pier.service;

import com.pier.rest.model.Benefit;

public interface BenefitDao extends GenericDao<Benefit, Long> {
	
	Boolean removeBenefit(Benefit benefit);

}

package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Promotion;
import com.pier.service.PromotionDao;

@Repository("PromotionDao")
public class PromotionDaoImpl extends HibernateDao<Promotion, Long> implements PromotionDao {

	@Override
	public boolean removePromotion(Promotion promotion) {
		delete(promotion);
		
		return true;
	}

}

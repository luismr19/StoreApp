package com.pier.service;

import com.pier.rest.model.Promotion;

public interface PromotionDao extends GenericDao<Promotion,Long>{
	
	boolean removePromotion(Promotion promotion);

}

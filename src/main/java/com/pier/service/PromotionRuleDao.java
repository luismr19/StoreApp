package com.pier.service;

import com.pier.rest.model.PromotionRule;

public interface PromotionRuleDao extends GenericDao<PromotionRule,Long>{
	
	boolean removePromotionRule(PromotionRule rule);

}

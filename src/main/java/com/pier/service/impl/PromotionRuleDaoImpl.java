package com.pier.service.impl;

import com.pier.rest.model.PromotionRule;
import com.pier.service.PromotionRuleDao;

public class PromotionRuleDaoImpl extends HibernateDao<PromotionRule, Long> implements PromotionRuleDao {

	@Override
	public boolean removePromotionRule(PromotionRule rule) {
		delete(rule);
		return true;
	}

}

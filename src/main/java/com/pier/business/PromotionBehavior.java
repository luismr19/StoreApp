package com.pier.business;

import com.pier.rest.model.Benefit;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;

public enum PromotionBehavior {
	
	TOTALDISCOUNT(1),FREE_GIFT(2),N_IS_LESS(3),BUY_ONE_GET_N(4);
	
	int identifier;
	
	private PromotionBehavior(int id){
		this.identifier=id;
	}
	
	public Benefit getGift(PurchaseOrder order, PromotionRule rule){
		
		BenefitCalculator calculator=new TotalDiscountCalculator(rule,order);
		
		return calculator.calculateBenefit();
	}

}

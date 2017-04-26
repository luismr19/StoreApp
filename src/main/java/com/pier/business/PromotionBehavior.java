package com.pier.business;

import com.pier.business.util.BenefitCalculator;
import com.pier.business.util.TotalBenefitCalculator;
import com.pier.rest.model.Benefit;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;

public enum PromotionBehavior {
	
	TOTALDISCOUNT(1),DISCOUNT_IN_Nth(2),DISCOUNT_Nth_SAME(3);
	
	int identifier;
	BenefitCalculator calculator;
	
	private PromotionBehavior(int id){
		this.identifier=id;
	}
	
	public Benefit getGift(PurchaseOrder order, PromotionRule rule){
				
		if(this.identifier==1){
		BenefitCalculator calculator=new TotalBenefitCalculator(rule,order);
		}
		
		return calculator.calculateBenefit();
	}

}

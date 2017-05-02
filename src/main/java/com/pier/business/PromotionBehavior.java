package com.pier.business;

import com.pier.rest.model.Benefit;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;

public enum PromotionBehavior {
	
	TOTALDISCOUNT(1),DISCOUNT_IN_Nth(2),DISCOUNT_Nth_SAME(3),GIVEAWAY_ON_SAME(4);
	
	int identifier;
	BenefitGiveAway calculator;
	
	private PromotionBehavior(int id){
		this.identifier=id;
	}
	
	public Benefit getGift(PurchaseOrder order, PromotionRule rule){
				
		if(this.identifier==1){
		calculator=new TotalBenefitGiveAway(rule,order);
		}else if(this.identifier==2){
		calculator=new InProductGiveAway(rule,order);	
		}else if(this.identifier==3){
		calculator=new InSameProductGiveAway(rule,order,true);		
		}else if(this.identifier==3){
		calculator=new InSameProductGiveAway(rule,order,false);		
		}
		
		return calculator.calculateBenefit();
	}

}

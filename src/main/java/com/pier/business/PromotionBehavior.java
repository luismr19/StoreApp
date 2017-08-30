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
	//total discount: only checks if product is contained in any of the rule arrays and minimums are met, applies discount in total sum
	//In Product give away is like total discount only that it only applies discount in the product of lowest price
	//In same product giveaway only applies disccount in products which quantity bought is greater or equal to the min amount
	//In same product giveaway with flag true: same as above but the giveaway will be a product equal to those that were eligible
	
	/* the categories, products, types, and brands arrays will always define what is eligible for promotion, if you want to
	 * apply a general promotion then add the "All" category to the categories array */
	
	/*in all four types the minimum purchase condition has to be met, "total" and "in product" behaviors are the same when the percentage
	 is 0*/
	
	/* you can always give products away and set points regardless of the percentage discount, except for the 
	 * "In same product giveaway with flag true" that automatically gives "one" product for each one that met the minimum amount
	 */
	
	public Benefit getGift(PurchaseOrder order, PromotionRule rule){
				
		if(this.identifier==1){			
		calculator=new TotalBenefitGiveAway(rule,order);
		}else if(this.identifier==2){
		calculator=new InProductGiveAway(rule,order);//	
		}else if(this.identifier==3){
		calculator=new InSameProductGiveAway(rule,order,false);		
		}else if(this.identifier==3){
		calculator=new InSameProductGiveAway(rule,order,true);	//duplicate true	
		}
		
		return calculator.calculateBenefit();
	}

}

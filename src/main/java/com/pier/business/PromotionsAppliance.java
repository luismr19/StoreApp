package com.pier.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pier.rest.model.Benefit;
import com.pier.rest.model.Promotion;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.PromotionDao;

@Component
public class PromotionsAppliance {
	
	@Autowired
	PromotionDao dao;
		
	
	public Benefit calculateBenefits(PurchaseOrder order){
		
		List<Promotion> activePromotions=fetchActivePromotions();
		Benefit totalBenefit=new Benefit();
		
		for(Promotion promo:activePromotions){
			
			PromotionRule rule=promo.getPromotionRule();
			
			if(!isPromotionApplied(totalBenefit) || promo.getInclusive()){
				
				Benefit currentBenefit=rule.getBehavior().getGift(order, rule);
				
				if(isPromotionApplied(totalBenefit)){
					totalBenefit.setDiscount(currentBenefit.getDiscount().add(totalBenefit.getDiscount()));
					totalBenefit.setPoints(totalBenefit.getPoints()+currentBenefit.getPoints());
					totalBenefit.getProducts().addAll(currentBenefit.getProducts());
				}else{
					if(!promo.getInclusive()){
						totalBenefit=currentBenefit;
						break;
					}
				}
				
				totalBenefit=currentBenefit;			
			}
		}
		
		return totalBenefit;
	}
	
	private List<Promotion> fetchActivePromotions(){
		List<Promotion> activePromotions=dao.list().stream().filter(p->p.getEnabled()).collect(Collectors.toList());
		
		return activePromotions;
	}
	
	private boolean isPromotionApplied(Benefit gift){
		if((gift.getDiscount().compareTo(BigDecimal.ZERO)>0)||gift.getPoints()>0L || gift.getProducts().size()>0){
			return true;
		}
		return false;
	}

}

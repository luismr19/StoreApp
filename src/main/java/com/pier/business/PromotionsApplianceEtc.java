package com.pier.business;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.pier.business.exception.OutOfStockException;
import com.pier.rest.model.Benefit;
import com.pier.rest.model.Promotion;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.PromotionDao;
import com.pier.service.impl.PromotionService;

@Component
public class PromotionsApplianceEtc {
	
	@Autowired
	PromotionDao dao;
	
	@Autowired
	CartOperationsDelegate cartOps;
	
	@Autowired
	PromotionService promotionSvc;	
		
	
	public Benefit calculateBenefits(PurchaseOrder order){
		
		List<Promotion> activePromotions=fetchActivePromotions();
		Benefit totalBenefit=new Benefit();
		
		for(Promotion promo:activePromotions){
			
			PromotionRule rule=promo.getPromotionRule();
			
			//if one promotion is applied another one can't be applied, unless the promotion is inclusive
			if(!isPromotionApplied(totalBenefit) || promo.getInclusive()){
				
				//does calculations to see what the gift will be 
				Benefit currentBenefit=rule.getBehavior().getGift(order, rule);
				
				
				//check benefit give away in case of out of stock products
				if(currentBenefit!=null) {
				long productOutOfStock=cartOps.isOutOfStockForGiveAway(currentBenefit.getProducts());
				if(productOutOfStock>0L){
					continue;
				}
				}
				
				//checks if there was indeed something to give away or if the order was elegible for a promotion
				//note that in the first iteration this will always be false
				if(isPromotionApplied(totalBenefit)){
				//if there was a previous benefit then add to the current points, discount products etc
					totalBenefit.setDiscount(currentBenefit.getDiscount().add(totalBenefit.getDiscount()));
					totalBenefit.setPoints(totalBenefit.getPoints()+currentBenefit.getPoints());
					totalBenefit.getProducts().addAll(currentBenefit.getProducts());
				}else{//if no promotion has been applied then try to apply it
					totalBenefit=currentBenefit;
				//checks if indeed something was given or the order was elegible for a promotion
				if(isPromotionApplied(totalBenefit)){
				//check if the promotion applied is inclusive (aka can go with other promotion or not)
					if(!promo.getInclusive()){						
						break; //if it's not inclusive then that's it, beak the cicle
					}
				}
				//continue the loop
				}				
							
			}
		
		}
		//if after everything else no promotion was applied we return a null object to avoid having empty entities in table
				
		return totalBenefit;
	}
	
	private List<Promotion> fetchActivePromotions(){
		/*List<Promotion> promotions=dao.list();
		List<Promotion> activePromotions=promotions.stream().filter(p->p.getEnabled())
				.filter(p->p.getStartDate().isBefore(LocalDateTime.now())).filter(p->p.getEndDate().isAfter(LocalDateTime.now()))
				.collect(Collectors.toList());
		
		return activePromotions;*/
		return promotionSvc.getActive();
	}
	
	public static boolean isPromotionApplied(Benefit gift){
		if(gift!=null){
		if((gift.getDiscount()!=null && gift.getDiscount().compareTo(BigDecimal.ZERO)>0)
				||(gift.getPoints()!=null && gift.getPoints()>0L) || (gift.getProducts()!=null && gift.getProducts().size()>0)){
			return true;
		}
		}
		return false;
	}
	
//same method as calculateBenefits but this one does not return the gift to apply but the list of promotions that were effective
public List<Promotion> getEffectivePromotions(PurchaseOrder order){
	
		List<Promotion> promotionsAdded=new ArrayList<>();
		List<Promotion> activePromotions=fetchActivePromotions();
		Benefit totalBenefit=new Benefit();
		
		for(Promotion promo:activePromotions){
			
			PromotionRule rule=promo.getPromotionRule();
			
			//if one promotion is applied another one can't be applied, unless the promotion is inclusive
			if(!isPromotionApplied(totalBenefit) || promo.getInclusive()){
				
				//does calculations to see what the gift will be 
				Benefit currentBenefit=rule.getBehavior().getGift(order, rule);
				
				//checks if there was indeed something to give away or if the order was elegible for a promotion
				//note that in the first iteration this will always be false
				if(isPromotionApplied(totalBenefit)){
				//if there was a previous benefit then add to the current points, discount products etc
					totalBenefit.setDiscount(currentBenefit.getDiscount().add(totalBenefit.getDiscount()));
					totalBenefit.setPoints(totalBenefit.getPoints()+currentBenefit.getPoints());
					totalBenefit.getProducts().addAll(currentBenefit.getProducts());
					
					promotionsAdded.add(promo);
				}else{//if no promotion has been applied then try to apply it				
					totalBenefit=currentBenefit;				
				if(isPromotionApplied(totalBenefit)){
				//check if the promotion applied is inclusive (aka can go with other promotion or not)
					promotionsAdded.add(promo);
					if(!promo.getInclusive()){						
						break; //if it's not inclusive then that's it, beak the cicle
					}
				}
				//continue the loop
				}				
							
			}
		}
		//if after everything else no promotion was applied we return a null object to avoid having empty entities in table
				
		return promotionsAdded;
	}
	
	public void updatedReachedCount(Promotion promotion){		
		promotion.setReached(promotion.getReached()+1);
		dao.update(promotion);
		
	}

}

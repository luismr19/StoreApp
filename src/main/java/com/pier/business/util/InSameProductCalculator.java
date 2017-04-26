package com.pier.business.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.pier.rest.model.Benefit;
import com.pier.rest.model.Product;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;

public class InSameProductCalculator implements BenefitCalculator {
	
	PromotionRule rule; 
	PurchaseOrder order;	
	List<Product> affectedProducts;	
	BigDecimal total;
	boolean willDuplicate;
	
	public InSameProductCalculator(PromotionRule rule, PurchaseOrder order, boolean willDuplicate) {
		super();
		this.rule = rule;
		this.order = order;
		this.willDuplicate=willDuplicate;
	}

	@Override
	public Benefit calculateBenefit() {
		
		List<Product> productsInOrder=OrderDetailUtil.getAsProductList(order.getPurchaseItems());
		
		//check if total purchase surpasses minimum required
		if(order.getTotal().compareTo(rule.getMinPurchase())>=0){
			Predicate<Product> isProductPresent=rule.getProducts()::contains;
			Predicate<Product> isInCategories=p->p.getCategories().stream().anyMatch(rule.getCategories()::contains);
			Predicate<Product> isInTypes=p->rule.getProductTypes().contains(p.getProductType());
			Predicate<Product> isInBrands=p->rule.getBrands().contains(p.getBrand());
			
			Predicate<Product> isEligibleForPromotion=isProductPresent.or(isInCategories).or(isInTypes).or(isInBrands);
			
			//this emulates a 2 x * promotion in which if the quantity is more than the specified the product is eligible
			affectedProducts=order.getPurchaseItems().stream()
					.filter(item->item.getQuantity()>=rule.getMinAmount())
					.map(item->item.getProduct())
					.filter(isEligibleForPromotion).collect(Collectors.toList());
			
		}else{
			total=BigDecimal.ZERO;
		}
		
		BigDecimal discount=total.multiply(new BigDecimal(1/rule.getPercentage()));
				
		Benefit result=new Benefit();
		result.setDiscount(discount);
        if(willDuplicate){
        	result.setProducts(affectedProducts);		
		}
        result.getProducts().addAll(rule.getGiveAway());
		result.setPoints(rule.getPoints());
		return result;
	}

}

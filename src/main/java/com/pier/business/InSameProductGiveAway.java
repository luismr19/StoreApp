package com.pier.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.pier.business.util.OrderDetailUtil;
import com.pier.rest.model.Benefit;
import com.pier.rest.model.Product;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;
/*
 *This is a 'buy N get 1 free'  or 'discount in Nth product' promotion type, depending on willDuplicate value
 * N is specified by minAmount,
 * it can give you a free product equal to the one(s) you bought or apply a percentage discount on the Nth product
 * if conditions are met;*/
public class InSameProductGiveAway implements BenefitGiveAway {
	
	PromotionRule rule; 
	PurchaseOrder order;	
	List<Product> affectedProducts;	
	BigDecimal total;
	boolean willDuplicate;
	
	public InSameProductGiveAway(PromotionRule rule, PurchaseOrder order, boolean willDuplicate) {
		super();
		this.rule = rule;
		this.order = order;
		this.willDuplicate=willDuplicate;
	}

	@Override
	public Benefit calculateBenefit() {
		
		List<Product> productsInOrder=OrderDetailUtil.getAsProductList(order.getPurchaseItems());
		BigDecimal discount=BigDecimal.ZERO;
		
		//check if total purchase surpasses minimum required
		if(order.getTotal().compareTo(rule.getMinPurchase())>=0){
			Predicate<Product> isProductPresent=rule.getProducts()::contains;
			Predicate<Product> isInCategories=p->p.getCategories().stream().anyMatch(rule.getCategories()::contains);
			Predicate<Product> isInTypes=p->rule.getProductTypes().contains(p.getProductType());
			Predicate<Product> isInBrands=p->rule.getBrands().contains(p.getBrand());
			
			Predicate<Product> isEligibleForPromotion=isProductPresent.or(isInCategories).or(isInTypes).or(isInBrands);
			
			//this emulates a buy N get 1 promotion in which if the quantity is more or equal to the specified the product is eligible
			affectedProducts=order.getPurchaseItems().stream()
					.filter(item->item.getQuantity()>=rule.getMinAmount())
					.map(item->item.getProduct())
					.filter(isEligibleForPromotion).collect(Collectors.toList());
			total=affectedProducts.stream().map(product->product.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
			
		}
		
		
		if(rule.getPercentage()!=0){
		discount=total.multiply(new BigDecimal(1/rule.getPercentage()));
		}		
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

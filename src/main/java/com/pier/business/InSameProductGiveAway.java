package com.pier.business;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.mysql.jdbc.StringUtils;
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
		
		List<Product> productsInOrder=OrderDetailUtil.getAsProductList(order.getOrderDetails())
				.stream().map(prodFlav->prodFlav.getProduct()).collect(Collectors.toList());
		BigDecimal discount=BigDecimal.ZERO;
		//check if order sponsorship is valid in case of any
		boolean isSponsoshipValid=((order.getPromoCodeEntry()!=null && rule.getPromotionCode()!=null) && order.getPromoCodeEntry().equals(rule.getPromotionCode())
				|| (StringUtils.isNullOrEmpty(order.getPromoCodeEntry()) && StringUtils.isNullOrEmpty(rule.getPromotionCode())));
		//check if order surpasses the min purchase
		boolean isCompliant=order.getTotal().compareTo(rule.getMinPurchase())>=0 ;	
			
		//check if total purchase surpasses minimum required
		if((isCompliant && isSponsoshipValid)){
			
			Predicate<Product> isProductPresent=rule.getProducts()::contains;
			Predicate<Product> isInCategories=p->p.getCategories().stream().anyMatch(rule.getCategories()::contains);
			Predicate<Product> isInTypes=p->rule.getProductTypes().contains(p.getProductType());
			Predicate<Product> isInBrands=p->rule.getBrands().contains(p.getBrand());
			
			Predicate<Product> isEligibleForPromotion=isProductPresent.or(isInCategories).or(isInTypes).or(isInBrands);
			
			//this emulates a buy N get 1 promotion in which if the quantity is more or equal to the specified the product is eligible
			affectedProducts=order.getOrderDetails().stream()
					.filter(item->item.getQuantity()>=rule.getMinAmount())
					.map(item->item.getProduct().getProduct())
					.filter(isEligibleForPromotion).collect(Collectors.toList());
			try{
			total=affectedProducts.stream().map(product->product.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
			}catch(NoSuchElementException no){
				return null;
			}
			
		}else{
			return null;
		}
		
		
		if(rule.getPercentage()!=0){
			discount=total.multiply(new BigDecimal(rule.getPercentage()).divide(new BigDecimal(100),2,RoundingMode.HALF_UP)).setScale(2, BigDecimal.ROUND_HALF_DOWN);
		}		
		Benefit result=new Benefit();
		result.setDiscount(discount);
		List productstoGive;
        if(willDuplicate){
          productstoGive=affectedProducts;		
		}else{
		  productstoGive=new ArrayList(rule.getGiveAway());
		}
        //this needs to be done since setting Benefit's products by directly passing PromotionRule's products is disallowed, hence ends up in removal of rule_products table
      
      	result.setProducts(productstoGive);      	
        result.getProducts().addAll(productstoGive);
		result.setPoints(rule.getPoints());
		return result;
	}

}

package com.pier.business;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.pier.business.util.BigDecimalComparator;
import com.pier.business.util.OrderDetailUtil;
import com.pier.rest.model.Benefit;
import com.pier.rest.model.Product;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;


/*
 * this type of promotion applies discount on the cheapest product
 * 
 * and/or gives specified points or product if at least one product is eligible*/
public class InProductGiveAway implements BenefitGiveAway{
	
	PromotionRule rule; 
	PurchaseOrder order;	
	List<Product> affectedProducts;
	BigDecimal total;
	
	public InProductGiveAway(PromotionRule rule, PurchaseOrder order) {
		super();
		this.rule = rule;
		this.order = order;
	}

	@Override
	public Benefit calculateBenefit() {
		
		List<Product> productsInOrder=OrderDetailUtil.getAsProductList(order.getOrderDetails())
				.stream().map(prodFlav->prodFlav.getProduct()).collect(Collectors.toList());
				
		
		BigDecimal discount=BigDecimal.ZERO;
		
				if(order.getTotal().compareTo(rule.getMinPurchase())>=0 && productsInOrder.size()>=rule.getMinAmount()){
					Predicate<Product> isProductPresent=rule.getProducts()::contains;
					Predicate<Product> isInCategories=p->p.getCategories().stream().anyMatch(rule.getCategories()::contains);
					Predicate<Product> isInTypes=p->rule.getProductTypes().contains(p.getProductType());
					Predicate<Product> isInBrands=p->rule.getBrands().contains(p.getBrand());
					
					Predicate<Product> isEligibleForPromotion=isProductPresent.or(isInCategories).or(isInTypes).or(isInBrands);
					
					affectedProducts=productsInOrder
							.stream().filter(isEligibleForPromotion).collect(Collectors.toList());
					
					//total is equal to the minimum price of the affected products
					try{
					total=affectedProducts.stream().map(product->product.getPrice()).min(new BigDecimalComparator()).get();
					}catch(NoSuchElementException no){
						return null;
					}
										
				}else{
					return null;
				}
				
							
				if(rule.getPercentage()!=0)
				discount=total.multiply(new BigDecimal(1).divide(new BigDecimal(rule.getPercentage()))).setScale(2, BigDecimal.ROUND_HALF_DOWN);
				
				
				Benefit result=new Benefit();
				result.setDiscount(discount);
				if(affectedProducts.size()>0){
				//this needs to be done since setting Benefit's products by directly passing PromotionRule's products is disallowed, hence ends up in removal of rule_products table
				ArrayList productstoGive=new ArrayList(rule.getGiveAway());
				result.setProducts(productstoGive);
				result.setPoints(rule.getPoints());
				}
				
				return result;
	}

}

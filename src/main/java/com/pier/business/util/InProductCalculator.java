package com.pier.business.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.pier.rest.model.Benefit;
import com.pier.rest.model.Product;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;

public class InProductCalculator implements BenefitCalculator{
	
	PromotionRule rule; 
	PurchaseOrder order;	
	List<Product> affectedProducts;
	BigDecimal total;
	
	public InProductCalculator(PromotionRule rule, PurchaseOrder order) {
		super();
		this.rule = rule;
		this.order = order;
	}

	@Override
	public Benefit calculateBenefit() {
		
		List<Product> productsInOrder=OrderDetailUtil.getAsProductList(order.getPurchaseItems());
		
				if(order.getTotal().compareTo(rule.getMinPurchase())>=0 && productsInOrder.size()>=rule.getMinAmount()){
					Predicate<Product> isProductPresent=rule.getProducts()::contains;
					Predicate<Product> isInCategories=p->p.getCategories().stream().anyMatch(rule.getCategories()::contains);
					Predicate<Product> isInTypes=p->rule.getProductTypes().contains(p.getProductType());
					Predicate<Product> isInBrands=p->rule.getBrands().contains(p.getBrand());
					
					Predicate<Product> isEligibleForPromotion=isProductPresent.or(isInCategories).or(isInTypes).or(isInBrands);
					
					affectedProducts=productsInOrder
							.stream().filter(isEligibleForPromotion).collect(Collectors.toList());
					
					total=affectedProducts.stream().map(product->product.getPrice()).min(new BigDecimalComparator()).get();
										
				}else{
					total=BigDecimal.ZERO;
				}
				
				BigDecimal discount=total.multiply(new BigDecimal(1/rule.getPercentage()));
						
				Benefit result=new Benefit();
				result.setDiscount(discount);
				result.setProducts(rule.getGiveAway());
				result.setPoints(rule.getPoints());
				return result;
	}

}

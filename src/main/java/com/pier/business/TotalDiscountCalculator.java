package com.pier.business;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

import com.pier.rest.model.Benefit;
import com.pier.rest.model.Product;
import com.pier.rest.model.PromotionRule;
import com.pier.rest.model.PurchaseOrder;

public class TotalDiscountCalculator implements BenefitCalculator{
	
	PromotionRule rule; 
	PurchaseOrder order;	
	List<Product> affectedProducts;
		

	public TotalDiscountCalculator(PromotionRule rule, PurchaseOrder order) {
		super();
		this.rule = rule;
		this.order = order;
	}


	@Override
	public Benefit calculateBenefit() {		
		
		affectedProducts=order.getPurchaseItems().stream()
				.map(item->item.getProduct()).filter(rule.getProducts()::contains).collect(Collectors.toList());
		
		BigDecimal total=affectedProducts.stream().map(product->product.getPrice()).reduce(BigDecimal.ZERO, BigDecimal::add);
		
		BigDecimal discount=total.multiply(new BigDecimal(1/rule.getPercentage()));
		
		//affectedProducts.addAll(order.getPurchaseItems().stream().map(item->item.getProduct()))
		
		Benefit result=new Benefit();
		result.setDiscount(discount);
		
		return result;
		
	}

}

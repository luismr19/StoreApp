package com.pier.business.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.Product;
import com.pier.rest.model.PurchaseOrder;

public class OrderDetailUtil {

	public static boolean mapToOrder(Product product, PurchaseOrder order) {
		
		if(!product.getEnabled() || product.getExistence()<1){
			return false;
		}
		List<OrderDetail> purchaseItems = order.getPurchaseItems();
		
		if (purchaseItems != null) {
			Optional<OrderDetail> detail = order.getPurchaseItems().stream()
					.filter(dt -> dt.getProduct().equals(product)).findFirst();
			if (detail.isPresent()) {
				detail.get().setQuantity(detail.get().getQuantity() + 1);
			}
			order.getPurchaseItems().add(new OrderDetail(1, product, order));

		} else {
			order.setPurchaseItems(Arrays.asList(new OrderDetail(1, product, order)));
		}
		BigDecimal total=BigDecimal.ZERO;
		for(OrderDetail detail:order.getPurchaseItems()){
			total=total.add(detail.getProduct().getPrice()).multiply(new BigDecimal(detail.getQuantity()));
		}
		order.setTotal(total);
		
		return true;
	}

	public static List<OrderDetail> generate(List<Product> products) {

		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

		PurchaseOrder order = new PurchaseOrder();

		for (Product product : products) {
			Optional<OrderDetail> detail = orderDetails.stream().filter(dt -> dt.getProduct().equals(product))
					.findFirst();
			if (detail.isPresent()) {
				detail.get().setQuantity(detail.get().getQuantity() + 1);
				orderDetails.add(detail.get());
			} else {
				orderDetails.add(new OrderDetail(1, product, order));
			}
		}

		return orderDetails;

	}

	public static PurchaseOrder getPurchaseOrder(List<OrderDetail> orderDetails) {
		return orderDetails.get(0).getOrder();
	}
	
	public static List<Product> getAsProductList(List<OrderDetail> orderDetails){
		List<Product> products=new ArrayList<Product>();
		for(OrderDetail detail:orderDetails){
			for(int i=0;i<detail.getQuantity();i++)
				products.add(detail.getProduct());
		}
		
		return products;
	}

}

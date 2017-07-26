package com.pier.business.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.PurchaseOrder;

public class OrderDetailUtil {

	public static boolean mapToOrder(ProductFlavor product, PurchaseOrder order, int quantity) {

		try {
			if (!product.getProduct().getEnabled() || product.getExistence() < 1) {
				return false;
			}
			Set<OrderDetail> purchaseItems = order.getPurchaseItems();

			if (purchaseItems != null) {
				Optional<OrderDetail> detail = order.getPurchaseItems().stream()
						.filter(dt -> dt.getProduct().equals(product)).findFirst();
				if (detail.isPresent()) {
					detail.get().setQuantity(detail.get().getQuantity() + quantity);
				} else {
					order.getPurchaseItems().add(new OrderDetail(1, product, order));
				}

			} else {
				order.setPurchaseItems(new HashSet(Arrays.asList(new OrderDetail(1, product, order))));
			}
			BigDecimal total = BigDecimal.ZERO;
			for (OrderDetail detail : order.getPurchaseItems()) {
				total = total.add(detail.getProduct().getProduct().getPrice()).multiply(new BigDecimal(detail.getQuantity()));
			}
			order.setTotal(total);
		} catch (Exception e) {
			return false;
		}
		return true;
	}

	public static List<OrderDetail> generate(List<ProductFlavor> products) {

		List<OrderDetail> orderDetails = new ArrayList<OrderDetail>();

		PurchaseOrder order = new PurchaseOrder();

		for (ProductFlavor product : products) {
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

	public static List<ProductFlavor> getAsProductList(Set<OrderDetail> orderDetails) {
		List<ProductFlavor> products = new ArrayList<ProductFlavor>();
		for (OrderDetail detail : orderDetails) {
			for (int i = 0; i < detail.getQuantity(); i++)
				products.add(detail.getProduct());
		}

		return products;
	}

	public static void removeProductFromDetails(Set<OrderDetail> ordersDetails, ProductFlavor product) {
		Optional<OrderDetail> detail = ordersDetails.stream().filter(dt -> dt.getProduct().equals(product)).findFirst();
		if (detail.isPresent()) {
			//if it's the last item then remove the whole reference
			if (detail.get().getQuantity() > 1) {
				detail.get().setQuantity(detail.get().getQuantity() - 1);
			} else {
				ordersDetails.remove(detail.get());
			}
		}
	}

}

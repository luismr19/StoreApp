package com.pier.business.util;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.hibernate.Session;
import org.hibernate.query.Query;

import com.pier.rest.model.OrderDetail;
import com.pier.rest.model.OrderDetailId;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductFlavor;
import com.pier.rest.model.PurchaseOrder;

public class OrderDetailUtil {

	public static OrderDetail mapToOrder(ProductFlavor product, PurchaseOrder order, int quantity) {		
		
		OrderDetail orderDetail=null;
		try {
			if (!product.getProduct().getEnabled() || product.getExistence() < 1) {
				return null;
			}
			Set<OrderDetail> purchaseItems = order.getOrderDetails();

			if (purchaseItems != null) {
				Optional<OrderDetail> detail = order.getOrderDetails().stream()
						.filter(dt -> dt.getProduct().equals(product)).findFirst();
				if (detail.isPresent()) {
					orderDetail=detail.get();
					orderDetail.setQuantity(orderDetail.getQuantity() + quantity);					
				} else {
					orderDetail=new OrderDetail(quantity, product, order);
					order.getOrderDetails().add(orderDetail);
				}

			} else {
				orderDetail=new OrderDetail(quantity, product, order);
				order.setOrderDetails(new HashSet(Arrays.asList(orderDetail)));
			}
			
			order.setTotal(updateTotals(order));
		} catch (Exception e) {
			return null;
		}
		return orderDetail;
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

	public static Set<OrderDetail> updateOrderDetailsQuantities(Set<OrderDetail> ordersDetails, List<OrderDetail> modifiedDetails) {
		
		Map<ProductFlavor,OrderDetail> resultMap=ordersDetails.stream().collect(Collectors.toMap(OrderDetail::getProduct, x->x));		
		
		
		for(OrderDetail modifiedDetail: modifiedDetails){
			OrderDetail item=resultMap.get(modifiedDetail.getProduct());
			if(item!=null){
				if(modifiedDetail.getQuantity()>0){
				item.setQuantity(modifiedDetail.getQuantity());				
				}else{					
					resultMap.remove(modifiedDetail.getProduct());
				}
			}
		}
		
		return new HashSet(resultMap.values());
		
	}
	
public static OrderDetail removeProductFromDetails(Set<OrderDetail> ordersDetails, ProductFlavor product) {		
		
		Optional<OrderDetail> optionalDetail = ordersDetails.stream().filter(dt -> dt.getProduct().equals(product)).findFirst();
		OrderDetail detail=null;
		if (optionalDetail.isPresent()) {
			detail=optionalDetail.get();
			//if it's the last item then remove the whole reference
			if (detail.getQuantity() > 1) {
				detail.setQuantity(detail.getQuantity() - 1);
			} else {
				detail.setQuantity(0);								
			}
		}
		return detail;
	}
	
	public static int updateQuantity(Session session,OrderDetail detail){
		Query query = session.createQuery("update OrderDetail detail set detail.quantity = :quantity "
		        + "where detail.id=:id");
		query.setParameter("quantity", detail.getQuantity());
		query.setParameter("id", detail.getId());
		
		return query.executeUpdate();
	}
	
	public static Set<OrderDetail> removeDetail(Set<OrderDetail> ordersDetails, OrderDetail detail){
		Set<OrderDetail> detailsNew=new HashSet();
		Iterator<OrderDetail> iterator=ordersDetails.iterator();
		while(iterator.hasNext()){
			OrderDetail orderDetail=iterator.next();
			if(orderDetail.equals(detail)){
				
			}else{
				detailsNew.add(orderDetail);
			}		
			
		}
		return detailsNew;
	}
	
	public static BigDecimal updateTotals(PurchaseOrder order){
		BigDecimal total = BigDecimal.ZERO;
		for (OrderDetail detail : order.getOrderDetails()) {
			total = total.add(detail.getProduct().getProduct().getPrice()).multiply(new BigDecimal(detail.getQuantity()));
		}
		return total;
	}

}

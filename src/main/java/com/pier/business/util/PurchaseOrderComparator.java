package com.pier.business.util;

import java.util.Comparator;

import com.pier.rest.model.PurchaseOrder;
import com.pier.rest.model.UserOrder;

public class PurchaseOrderComparator implements Comparator<PurchaseOrder> {

	@Override
	public int compare(PurchaseOrder o1, PurchaseOrder o2) {
		// TODO Auto-generated method stub
		return o1.getPurchaseDate().compareTo(o2.getPurchaseDate());
	}

	

}

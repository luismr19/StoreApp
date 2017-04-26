package com.pier.business.util;

import java.math.BigDecimal;
import java.util.Comparator;

public class BigDecimalComparator implements Comparator<BigDecimal> {

	@Override
	public int compare(BigDecimal o1, BigDecimal o2) {		
	            return o1.compareTo(o2);     
	 }

}

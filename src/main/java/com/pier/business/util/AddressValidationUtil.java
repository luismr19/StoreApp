package com.pier.business.util;

import com.pier.rest.model.Address;

public class AddressValidationUtil {
	
	public static boolean isAddressValid(Address address){
		
		return address.getCity()!=null && address.getCity()!=null && 
				address.getCity().length()>1 && address.getState().length()>2 &&
				address.getCountry()!=null &&
				address.getCountry().length()>2 &&
				address.getDistrict()!=null &&
				address.getDistrict().length()>2 &&
				address.getNumber()!=null &&
				address.getStreet()!=null &&
				address.getStreet().length()>2 &&
				address.getZipCode()!=null &&
				address.getZipCode()>99;
				
	}

}

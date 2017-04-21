package com.pier.service;

import com.pier.rest.model.Address;

public interface AddressDao extends GenericDao<Address, Long> {
	
	boolean removeAddress(Address address);

}

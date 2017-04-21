package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Address;
import com.pier.service.AddressDao;

@Repository("addresDao")
public class AddressDaoImpl extends HibernateDao<Address,Long> implements AddressDao{

	@Override
	public boolean removeAddress(Address address) {
		delete(address);
		return true;
	}

}

package com.pier.service.impl;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import com.pier.DomainAwareBase;
import com.pier.rest.model.Address;
import com.pier.service.AddressDao;


public class AddressDaoImplTest extends DomainAwareBase{
	
	@Autowired
	AddressDao addressDao;
	
	Address newAddress(){
		Address addr=new Address();
		addr.setCountry("Macao");
		addr.setDistrict("lachava");
		addr.setNumber(15);
		addr.setState("lacochina");
		addr.setStreet("elpiiolo");
		addr.setZipCode(4578);		
		
		return addr;
	}

	@Test
	public void testRemoveAddress() {
		fail("Not yet implemented");
	}

	@Test
	public void testAdd() {
		int size=addressDao.list().size();
		addressDao.add(newAddress());
		assertTrue(addressDao.list().size()>size);
	}

	@Test
	public void testUpdate() {
		Address address=newAddress();
		addressDao.add(address);
		address.setCountry("Cochinao");
		addressDao.update(address);
		
		Address found=addressDao.find(address.getId());
		assertEquals(found.getCountry(),"Cochinao");		
		
	}	

	/*@Test
	public void testList() {
		List<Address> addressList=Arrays.asList(
				new Address("USA","Michigan","theStreet","porai",4487,14),
				new Address("Russia","Michigo","theStreet2","porahi",4587,14)); 
		
		for(Address a: addressList)
			addressDao.add(a);
		List<Address> list2=addressDao.list();		
		assertTrue(list2.get(list2.size()-1).getCountry().equals(addressList.get(1).getCountry()));
	}*/

	@Test
	public void testFind() {		
		assertTrue(addressDao.find("country", "Macao").size()>0);		
		
	}

}

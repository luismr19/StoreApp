package com.pier.service;

import com.pier.rest.model.Flavor;

public interface FlavorDao  extends GenericDao<Flavor,Long>{ 
	
	boolean removeFlavor(Flavor flavor);

}

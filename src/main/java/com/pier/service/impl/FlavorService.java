package com.pier.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pier.rest.model.Flavor;
import com.pier.service.FlavorDao;

@Service
public class FlavorService {
	
	@Autowired
	FlavorDao dao;
	
	public Flavor generateFlavor(String name, Long existence){
		
		Flavor flavor=dao.find("flavorName",name).stream().findFirst().orElse(new Flavor(name,existence));
		
		return flavor;
		
	}
	
	public List<Flavor> persistFlavors(Flavor...flavors){
		List flavorese=new ArrayList();
		for(Flavor flavor:flavors){
			dao.add(flavor);
			flavorese.add(flavor);
		}
		return flavorese;
	}

}

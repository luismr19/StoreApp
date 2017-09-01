package com.pier.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.pier.rest.model.Flavor;
import com.pier.rest.model.ProductFlavor;
import com.pier.service.FlavorDao;

@Service
public class FlavorService {
	
	@Autowired
	FlavorDao dao;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	public Flavor generateFlavor(String name, Long existence){
		
		Flavor flavor=dao.find("flavorName",name).stream().findFirst().orElse(new Flavor(name,existence));
		flavor.setExistence(existence);
		dao.add(flavor);
		return flavor;
		
	}
	
	public List<Flavor> persistFlavors(Flavor...flavors){
		List flavorese=new ArrayList();
		for(Flavor flavor:flavors){
			if(flavor.getId()==null)
			dao.update(flavor);
			flavorese.add(flavor);
		}
		return flavorese;
	}
	
	public List<ProductFlavor> findProductFlavor(long productId){
		//Criteria criteria = currentSession().createCriteria(ProductFlavor.class);		
		String hql = "FROM ProductFlavor as flavor where flavor.id.product.id = :id";
		
		Query query = currentSession().createQuery(hql);
		query.setParameter("id", productId);
		//criteria.add(Restrictions.eq("id.product.id", productId));
		
		List<ProductFlavor> results=query.list();
		
		return results;
	}

}

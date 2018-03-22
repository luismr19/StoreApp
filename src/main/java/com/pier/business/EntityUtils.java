package com.pier.business;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Transactional	
public class EntityUtils {
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getIdsForEntity(Class<?> clazz,String property, Object value){
		return sessionFactory.getCurrentSession().createCriteria(clazz).add(
				Restrictions.eq(property, value)).setProjection(Projections.property("id")).list();
		
	}
	
	@SuppressWarnings("unchecked")
	public <T> List<T> getIdsForEntity(Class<?> clazz){
		return sessionFactory.getCurrentSession().createCriteria(clazz).setProjection(Projections.property("id")).list();
		
	}

}

package com.pier.service.impl;

import java.io.Serializable;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.transaction.annotation.Propagation;

import com.pier.model.security.User;
import com.pier.service.GenericDao;

@Transactional(propagation= Propagation.REQUIRED, readOnly=false)
public class HibernateDao<E, K extends Serializable> implements GenericDao<E, K> {
	
	private SessionFactory sessionFactory;
	
	private Class<? extends E> daoType;  
	
	public HibernateDao(){
		daoType=getEntityClass();
	}
	
	@SuppressWarnings("unchecked")
	protected Class<E> getEntityClass() {
		@SuppressWarnings("rawtypes")
		Class<? extends HibernateDao> classy=getClass();
		Class<?>[] classes=GenericTypeResolver.resolveTypeArguments(classy, GenericDao.class);
        return (Class<E>)classes[0];
   }
	
	@Autowired
	public void setSessionFactory(SessionFactory sessionFactory){
		this.sessionFactory=sessionFactory;
	}
	
	public Session currentSession(){
		return sessionFactory.getCurrentSession();
	}

	@Override
	public void add(E entity) {
		currentSession().save(entity);
		
	}

	@Override
	public void update(E entity) {
		currentSession().saveOrUpdate(entity);
		
	}

	@Override
	public void delete(E entity) {
		currentSession().delete(entity);
		
	}
	

	@SuppressWarnings("unchecked")
	@Override
	public List<E> list() {		
		return currentSession().createCriteria(daoType).list();
	}

	@SuppressWarnings("unchecked")
	@Override
	public E find(K key) {		
		return (E)currentSession().get(daoType,key);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<E> find(String property, Object value) {
		Criteria criteria = currentSession().createCriteria(getEntityClass());
		List<E> results=(List<E>)criteria.add(Restrictions.eq(property, value)).list();
		return results;
		                            
	}
	
	

}

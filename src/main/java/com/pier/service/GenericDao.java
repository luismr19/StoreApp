package com.pier.service;

import java.util.List;

import org.hibernate.Session;

public interface GenericDao<E,K> {
	
	void add(E entity);
	
	void update(E entity);
	
	void delete(E entity);
	
	E find(K key);
	
	E lazyFind(K key);
	
	List<E> find(String property, Object value);
	
	List<E> list();
	
	public Session currentSession();

}

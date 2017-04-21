package com.pier.service.impl;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.pier.model.security.User;
import com.pier.security.JwtUserFactory;

@Transactional(propagation= Propagation.REQUIRED)
@Service
public class JwtUserDetailsServiceImpl implements UserDetailsService {
	@Autowired
	SessionFactory sessionfactory;
	
	@Override	
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {				
		Session session=sessionfactory.getCurrentSession();
		Criteria criteria = session.createCriteria(User.class);
		User user = (User) criteria.add(Restrictions.eq("username", username))
                .uniqueResult();
		
		return JwtUserFactory.create(user);
	}

}

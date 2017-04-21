package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.model.security.Authority;
import com.pier.service.AuthorityDao;

@Repository("authorityDao")
public class AuthorityDaoImpl extends HibernateDao<Authority, Long> implements AuthorityDao{

	@Override
	public boolean removeAuthority(Authority auth) {
		delete(auth);
		return true;
	}

}

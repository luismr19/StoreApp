package com.pier.service;

import com.pier.model.security.Authority;

public interface AuthorityDao extends GenericDao<Authority, Long> {
	
	boolean removeAuthority(Authority auth);

}

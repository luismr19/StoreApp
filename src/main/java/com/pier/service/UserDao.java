package com.pier.service;

import com.pier.model.security.User;

public interface UserDao extends GenericDao<User, Long>{
	
	boolean removeUser(User user);

}

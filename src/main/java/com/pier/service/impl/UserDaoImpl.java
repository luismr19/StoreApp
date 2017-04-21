package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.model.security.User;
import com.pier.service.UserDao;

@Repository("UserDao")
public class UserDaoImpl extends HibernateDao<User, Long> implements UserDao {

	@Override
	public boolean removeUser(User user) {
		delete(user);
		return true;
	}

}

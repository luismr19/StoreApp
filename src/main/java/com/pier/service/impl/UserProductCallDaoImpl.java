package com.pier.service.impl;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.UserProductCall;
import com.pier.service.UserProductCallDao;

@Repository("UserProductCallDao")
public class UserProductCallDaoImpl extends HibernateDao<UserProductCall,Long> implements UserProductCallDao {

	@Override
	public boolean removeUserProductCall(UserProductCall userProductCall) {
		delete(userProductCall);
		return true;
	}

	

}

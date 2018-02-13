package com.pier.service.impl;

import com.pier.rest.model.UserProductCall;
import com.pier.service.UserProductCallDao;

public class UserProductCallDaoImpl extends HibernateDao<UserProductCall,Long> implements UserProductCallDao {

	@Override
	public boolean removeUserProductCall(UserProductCall userProductCall) {
		delete(userProductCall);
		return true;
	}

	

}

package com.pier.service;

import com.pier.rest.model.UserProductCall;

public interface UserProductCallDao  extends GenericDao<UserProductCall, Long>{
	
	boolean removeUserProductCall(UserProductCall userProductCall);

}

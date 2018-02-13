package com.pier.service.impl;

import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;


import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductCallStatus;
import com.pier.rest.model.UserProductCall;
import com.pier.service.UserProductCallDao;

public class UserProductCallService {
	
	@Autowired
	UserProductCallDao dao;
	
	public List<UserProductCall> findUserProductCalls(int index,String status,String from,String to){
		int pageSize = 30;
		// this is using the one and only ugly criteria builder
		CriteriaBuilder criteriaBuilder = dao.currentSession().getCriteriaBuilder();
		CriteriaQuery<UserProductCall> productCallQuery = criteriaBuilder.createQuery(UserProductCall.class);
		
		LocalDate fromDate = LocalDate.now().minusMonths(2);
		LocalDate toDate = LocalDate.now();
		
		List<Predicate> conditionsList = new ArrayList<Predicate>();
		
		Root<UserProductCall> rootProductCall = productCallQuery.from(UserProductCall.class);
		
		if (from != null) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			fromDate = LocalDate.parse(from, formatter);
		}

		if (to != null) {
			final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			toDate = LocalDate.parse(to, formatter);
		}
		
		Predicate dateBetween =  criteriaBuilder.between(rootProductCall.<LocalDate>get("callDate"), fromDate, toDate);
		conditionsList.add(dateBetween);
		
		if(status!=null){

			if (status.equals(ProductCallStatus.ADDRESSED.name())) {
				Predicate addressed = criteriaBuilder.equal(rootProductCall.get("status"), ProductCallStatus.ADDRESSED);
				conditionsList.add(addressed);
			} else if (status.equals(ProductCallStatus.PENDING.name())) {
				Predicate pending = criteriaBuilder.equal(rootProductCall.get("status"), ProductCallStatus.PENDING);
				conditionsList.add(pending);
			}else if(status.equals(ProductCallStatus.DISABLED.name())){
				Predicate disabled = criteriaBuilder.equal(rootProductCall.get("status"), ProductCallStatus.DISABLED);
				conditionsList.add(disabled);
			}
			}
		
		javax.persistence.criteria.Order callDateOrder = criteriaBuilder.desc(rootProductCall.get("callDate"));
		productCallQuery.orderBy(callDateOrder);
		
		List<UserProductCall> results = dao.currentSession()
				.createQuery(productCallQuery.select(rootProductCall).where(conditionsList.toArray(new Predicate[] {})))
				.setFirstResult(index).setMaxResults(pageSize).getResultList();

		return results;
	}
	
	public UserProductCall newProductCall(Product product, User caller){
		
			UserProductCall productCall=new UserProductCall();
			
			if(product!=null)
				productCall.setProduct(product);
			
			productCall.setCaller(caller);
			productCall.setStatus(ProductCallStatus.PENDING);
			productCall.setCallDate(LocalDate.now());
			
			dao.add(productCall);
		
			return productCall;
		
	}

}

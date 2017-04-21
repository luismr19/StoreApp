package com.pier.business.validation;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.pier.rest.model.ObjectModel;
import com.pier.service.GenericDao;

public class IntegrityCheckerImpl<E extends ObjectModel,T extends GenericDao> implements IntegrityChecker<E, T > {
	
	@Autowired
	T dao;
	List<String> errors;
 
	public IntegrityCheckerImpl(){
		errors=new ArrayList<String>();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean checkIfExists(E model) {
		return dao.find(model.getId())!=null;
	}
	
	@Override
	public boolean checkIfDuplicate(E model) {		
		return false;
	}

	@Override
	public boolean checkIfValid(E model) {
		return true;
	}
	
	public List<String> getErrors(){
		return errors;
	}
	
	public void setErrors(List<String> errors){
		this.errors=errors;
	}

	

}

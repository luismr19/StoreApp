package com.pier.business.validation;

import java.util.List;

public interface IntegrityChecker<E, T> {	
	
	public boolean checkIfExists(E model);
	
	public boolean checkIfDuplicate(E model);
	
	public boolean checkIfValid(E model);
	
	public List<String> getErrors();
	

}

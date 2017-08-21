package com.pier.business.validation;

import com.pier.model.security.User;
import com.pier.service.UserDao;
import com.pier.service.impl.UserDaoImpl;

import java.util.Collections;

import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;;

@Component()
@Scope("prototype")
public class UserIntegrityChecker extends IntegrityCheckerImpl<User, UserDao> {
	
	@Autowired
	UserDao userDao;
	
	public UserIntegrityChecker(){
		super();
	}
	
	@Override
	public boolean checkIfValid(User user){
		errors.clear();
		return checkEmail(user.getEmail())				
				&&checkUsername(user.getUsername())
				&&checkFirstName(user.getFirstname())
				&&checLastName(user.getLastname());
	}
	
	@Override
	public boolean checkIfDuplicate(User user){
		errors.clear();
		
		boolean idExists=user.getId()!=null && (userDao.find("id", user.getId()).size()>0);
		boolean result= (userDao.find("email", user.getEmail()).size()>0)||(userDao.find("username", user.getUsername()).size()>0)	|| idExists;
		if(result)
		errors.add("email or username already exist");
		return result;
	}
	
	private boolean checkEmail(String email){
		EmailValidator validator=EmailValidator.getInstance(false, true);
		boolean result=validator.isValid(email);
		if(!result){
			errors.add("invalid email");
		}
		return result;
		
	}
	
	private boolean checkUsername(String username){
		UsernameValidator validator=new UsernameValidator();
		boolean result=validator.validate(username);
		if(!result){
			errors.add("invalid username");
		}
		return result;
	}
	
	private boolean checkFirstName(String name){
		boolean result=FullNameValidator.validateFirstName(name);
		if(!result){
			errors.add("invalid first name");
		}
		return result;
	}
	
	private boolean checLastName(String lastName){
		boolean result=FullNameValidator.validateLastName(lastName);
		if(!result){
			errors.add("invalid last name");
		}
		return result;
	}
	
	

}

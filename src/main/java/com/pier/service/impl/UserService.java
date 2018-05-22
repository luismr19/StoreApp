package com.pier.service.impl;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;

import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.security.JwtUser;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.UserDao;



@Transactional
@Component
public class UserService {
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	public Product addToFavorites(Product product, String token){
        User user=getUserFromTokenWithFavs(token);
        
		Set<Product> favorites=user.getFavorites();
		if(favorites.add(product)){
			userDao.update(user);
			return product;
		}
		
		return null;
	}
	
	public Set<Product> getFavorites(User user){
		return user.getFavorites();
	}
	
	public JwtUser getJwtUserFromToken(String token){
		
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		JwtUser user=(JwtUser)userDetailsService.loadUserByUsername(username);
		
		return user;
	}
	
	public User getUserFromToken(String token){
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		User user=userDao.find("username",username).get(0);
		
		return user;
	}
	
	public User getUserFromTokenWithFavs(String token){
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		User user=userDao.find("username",username).get(0);
		Hibernate.initialize(user.getFavorites());
		
		return user;
	}
	
	public User getUserFromTokenWithArticles(String token){
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		User user=userDao.find("username",username).get(0);
		Hibernate.initialize(user.getArticles());
		
		return user;
	}

	public Product removeFromFavorites(Product removedProduct, String token) {
		  User user=getUserFromTokenWithFavs(token);
	        
			Set<Product> favorites=user.getFavorites();
			if(favorites.remove(removedProduct)){
				userDao.update(user);
				return removedProduct;
			}
			
			return null;
	}
	
	public User updateAddressAndPassword(User user, String token){
		User currentUser=getUserFromToken(token);
		currentUser.setAddress(user.getAddress());
		if(user.getPassword()!=null && !user.getPassword().isEmpty()){
			currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
			currentUser.setLastPasswordResetDate(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		}
		EmailValidator validator=EmailValidator.getInstance(false, true);
		boolean validEmail=validator.isValid(user.getEmail());
		if(validEmail)		
		currentUser.setEmail(user.getEmail());
		currentUser.setPhoneNumber(user.getPhoneNumber());
		
		userDao.update(currentUser);
		
		return currentUser;
	}
	
	public User updateAddress(User user, String token){
		User currentUser=getUserFromToken(token);
		currentUser.setAddress(user.getAddress());		
		
		userDao.update(currentUser);
		
		return currentUser;
	}
	
	public User updatePassword(User user, String token){
		User currentUser=getUserFromToken(token);
		currentUser.setPassword(passwordEncoder.encode(user.getPassword()));		
		currentUser.setLastPasswordResetDate(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		
		userDao.update(currentUser);
		
		return currentUser;
	}

}

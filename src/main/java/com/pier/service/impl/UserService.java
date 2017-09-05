package com.pier.service.impl;

import java.util.Set;

import org.hibernate.Hibernate;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetailsService;
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
	
	public User getUserFromTokenWithFavs(String token){
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		User user=userDao.find("username",username).get(0);
		Hibernate.initialize(user.getFavorites());
		
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

}

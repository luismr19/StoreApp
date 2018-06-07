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

import com.pier.business.util.EmailService;
import com.pier.business.util.RandomGenerator;
import com.pier.business.validation.UserIntegrityChecker;
import com.pier.model.security.AuthorityName;
import com.pier.model.security.User;
import com.pier.rest.model.Product;
import com.pier.security.JwtUser;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.AuthorityDao;
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
	
	@Autowired
	private AuthorityDao authDao;
	
	@Autowired
	EmailService emailSvc;
	
	@Autowired
	UserIntegrityChecker userCheker;
	
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
	
	public User createUser(User user) {
		if(!userCheker.checkIfDuplicate(user) && userCheker.checkIfValid(user)) {
		user.setUsername(user.getUsername().toLowerCase());
		user.setEmail(user.getEmail().toLowerCase());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreatedDate(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		user.setLastPasswordResetDate(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		user.setAuthorities(authDao.find("name", AuthorityName.ROLE_USER));
		user.setPhoneNumber(user.getPhoneNumber());
		if(user.isSocialAcc()) {
		user.setEnabled(true);
		}else {
			user.setEnabled(false);	
			user.setVerifyToken(RandomGenerator.generateText(40,true));			
			
		}
		userDao.add(user);
		if(!user.getEnabled()) {
		try {
			emailSvc.sendSimpleMessage(user.getEmail(), "Bienvenido a Mxphysique.com", "Hola "+user.getFirstname()+" gracias por registrarte en Mxphysique.com, para poder ingresar activa tu cuenta siguiendo el siguiente enlace http://www.mxphysique.com/activate?verify_token="+user.getVerifyToken());
			}catch(Exception e) {
				// do nothing
			}
		}
		
		return user;
		}else {
			return null;
		}
	}
	
	public User updateUser(User currentUser, User newUser) {
		if(userCheker.checkIfValid(newUser)){
		currentUser.setFirstname(newUser.getFirstname());	        
        currentUser.setLastname(newUser.getLastname());
        currentUser.setEnabled(newUser.getEnabled());
        currentUser.setPassword(passwordEncoder.encode(newUser.getPassword()));
        currentUser.setAddress(newUser.getAddress());
        currentUser.setEmail(newUser.getEmail());	       
        
        userDao.update(currentUser);
        return currentUser;
		}
		
		return null;
        
        
		
	}
	
	public User sendActivationEmail(String email) {
		User user=null;	
		
		try {
			user=userDao.find("email",email).get(0);
			if(!user.getEnabled())
			emailSvc.sendSimpleMessage(user.getEmail(), "Bienvenido a Mxphysique.com", "Hola "+user.getFirstname()+" gracias por registrarte en Mxphysique.com, para poder ingresar activa tu cuenta siguiendo el siguiente enlace http://www.mxphysique.com/activate?verify_token="+user.getVerifyToken());
			return user;
		}catch(Exception ex) {
			return null;
		}
		
		
	}
	
	public User activateUser(String verifyToken) {
		User user=null;
		try {
		user=userDao.find("verifyToken", verifyToken).get(0);
		user.setEnabled(true);
        userDao.update(user);
		}catch(Exception e) {
			return null;
		}
        return user;
	}

}

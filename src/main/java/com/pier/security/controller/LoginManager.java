package com.pier.security.controller;

import java.io.IOException;

import org.apache.commons.lang.RandomStringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.pier.business.util.RandomGenerator;
import com.pier.model.security.User;
import com.pier.security.AuthenticationRequest;
import com.pier.service.impl.UserService;

@Component
public class LoginManager {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UserDetailsService userDetailsService;
	

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	ObjectMapper jsonMapper;
	
	public UserDetails handleSocialAuthentication(AuthenticationRequest request,User user) {
		UserDetails userDetails=null;
		// check if user logged in using facebook
				if (request.getSocialAccessToken() != null && request.getSocialAccessToken().length() > 0) {
					RestTemplate restTemplate = new RestTemplate();
					String authVerify = "https://graph.facebook.com/me?fields=first_name,middle_name,last_name,email&access_token=" + request.getSocialAccessToken();
					ResponseEntity<String> response = restTemplate.getForEntity(authVerify, String.class);
					if (response.getStatusCode().equals(HttpStatus.OK)) {	
						
						try {
							JsonNode userObject=jsonMapper.readTree(response.getBody());
							
							String email=userObject.get("email").textValue();
							String first_name=userObject.get("first_name").textValue();
							String middle_name=userObject.get("middle_name")!=null?userObject.get("middle_name").textValue():"";	
							String last_name=userObject.get("last_name").textValue();	
							
							//normally if the user logs in using facebook the username field has the email
							if(!email.equals(request.getUsername()))
								return null;
							
							//if user was not found in the previous step create a new one
							if(user!=null) {
								userDetails = userDetailsService.loadUserByUsername(user.getUsername());
								}else{
									User newUser=createSocialAccount(email,first_name,middle_name+" "+last_name);
									userDetails = userDetailsService.loadUserByUsername(newUser.getUsername());
								}
							
						} catch (IOException e) {
							return null;								
						}	
					}
					
				} 
				
				return userDetails;
	}
	
	public void authenticateUser(UserDetails userDetails,AuthenticationRequest request) {
		AbstractAuthenticationToken authToken;
		//if the authentication was through a social app then authenticate using this
		if (userDetails!=null) {
			authToken = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			SecurityContextHolder.getContext().setAuthentication(authToken);
		} else {
		//if the authentication was with username and password
			authToken = new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword());

			final Authentication authentication = authenticationManager.authenticate(authToken);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
	}
	
	private User createSocialAccount(String email,String firstName,String lastName) {
		User newUser=new User();		
		String pwd=RandomGenerator.generateText(7,false);
		newUser.setUsername(email);
		newUser.setPassword(pwd);
		newUser.setEmail(email);
		newUser.setFirstname(firstName);
		newUser.setLastname(lastName);
		newUser.setSocialAcc(true);
		userService.createUser(newUser);
		
		return newUser;
	}

}

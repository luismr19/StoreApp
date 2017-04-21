package com.pier.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pier.model.security.User;
import com.pier.security.JwtUser;
import com.pier.security.JwtUserFactory;
import com.pier.security.util.JwtTokenUtil;

@RestController
public class UserRestController {
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
	private UserDetailsService userDetailsService;
	
	@RequestMapping(value="/user", method=RequestMethod.GET)
	public JwtUser getauthenticatedUser(HttpServletRequest request){
		String token=request.getHeader(tokenHeader);
		String username=jwtTokenUtil.getUsernameFromToken(token);		
		JwtUser user=(JwtUser)userDetailsService.loadUserByUsername(username);
		
		return user;
	}

}

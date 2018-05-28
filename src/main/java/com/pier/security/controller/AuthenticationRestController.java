package com.pier.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.pier.model.security.User;
import com.pier.security.AuthenticationRequest;
import com.pier.security.JwtAuthenticationResponse;
import com.pier.security.JwtUser;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.UserDao;
import com.pier.service.impl.UserService;

@RestController
public class AuthenticationRestController {

	@Value("${jwt.header}")
	private String tokenHeader;


	@Autowired
	private UserDao userDao;

	@Autowired
	private SessionFactory sessionfactory;

	@Autowired
	private JwtTokenUtil jwtTokenUtil;

	@Autowired
	private UserDetailsService userDetailsService;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	LoginManager loginManager;

	@RequestMapping(value = "/login", method = RequestMethod.POST)
	public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request, Device device) {

		EmailValidator validator = EmailValidator.getInstance(false, true);
		boolean result = validator.isValid(request.getUsername());
		User user = null;			
		UserDetails userDetails = null;

		try {
		//try to get the user by email	
		if (result)
			user = userDao.find("email", request.getUsername()).get(0);
		else if (user == null)
		//try to get user by username
			user = userDao.find("username", request.getUsername()).get(0);
		}//if user is not found
		catch(IndexOutOfBoundsException ex){
			// do nothing
		}

		 userDetails=loginManager.handleSocialAuthentication(request, user);

		if (user != null && user.getEnabled()) {
			request.setUsername(user.getUsername());
			
			loginManager.authenticateUser(userDetails,request);

			final String token = jwtTokenUtil.generateToken(request.getUsername(), device);

			return ResponseEntity.ok(new JwtAuthenticationResponse(token));
		}
		return new ResponseEntity(HttpStatus.BAD_REQUEST);

	}

	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
	public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
		String token = request.getHeader(tokenHeader);
		String username = jwtTokenUtil.getUsernameFromToken(token);
		// we use JwtUser rather than User because we don't want to expose the real User
		// entity as a best practice
		JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

		if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
			String refreshedToken = jwtTokenUtil.refreshToken(token);
			return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
		} else {
			return ResponseEntity.badRequest().body(null);
		}
	}
}

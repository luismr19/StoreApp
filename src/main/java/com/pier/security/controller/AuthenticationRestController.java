package com.pier.security.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.EmailValidator;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mobile.device.Device;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.pier.model.security.User;
import com.pier.security.AuthenticationRequest;
import com.pier.security.JwtAuthenticationResponse;
import com.pier.security.JwtUser;
import com.pier.security.util.JwtTokenUtil;
import com.pier.service.UserDao;



@RestController
public class AuthenticationRestController {

	@Value("${jwt.header}")
    private String tokenHeader;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private UserDao userDao;
			
	@Autowired
	private SessionFactory sessionfactory;
	
	@Autowired
	private JwtTokenUtil jwtTokenUtil;
	
	@Autowired
    private UserDetailsService userDetailsService;
	
	
	@RequestMapping(value="/login", method=RequestMethod.POST)
	public ResponseEntity<?> authenticateUser(@RequestBody AuthenticationRequest request, Device device){
		
		EmailValidator validator=EmailValidator.getInstance(false, true);
		boolean result=validator.isValid(request.getUsername());
		User user=null;
		if(result)
			user=userDao.find("email", request.getUsername()).get(0);		
		else if(user==null)
			user=userDao.find("username", request.getUsername()).get(0);
		//User user=userDao.find(new Long(1));
		
		if(user!=null && user.getEnabled()){
			request.setUsername(user.getUsername());
			final Authentication authentication=authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(request.getUsername(),request.getPassword()));
			
			SecurityContextHolder.getContext().setAuthentication(authentication);
			
			
			final String token=jwtTokenUtil.generateToken(request.getUsername(), device);	
			
			return ResponseEntity.ok(new JwtAuthenticationResponse(token));
		}
	     return new ResponseEntity(HttpStatus.BAD_REQUEST);
		
	}
	
	@RequestMapping(value = "/refresh", method = RequestMethod.GET)
    public ResponseEntity<?> refreshAndGetAuthenticationToken(HttpServletRequest request) {
        String token = request.getHeader(tokenHeader);
        String username = jwtTokenUtil.getUsernameFromToken(token);
        //we use JwtUser rather than User because we don't want to expose the real User entity as a best practice
        JwtUser user = (JwtUser) userDetailsService.loadUserByUsername(username);

        if (jwtTokenUtil.canTokenBeRefreshed(token, user.getLastPasswordResetDate())) {
            String refreshedToken = jwtTokenUtil.refreshToken(token);
            return ResponseEntity.ok(new JwtAuthenticationResponse(refreshedToken));
        } else {
            return ResponseEntity.badRequest().body(null);
        }
    }
}

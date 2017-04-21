package com.pier.security.filters;

import java.io.IOException;
import java.util.Date;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.pier.security.util.JwtTokenUtil;

public class JwtAuthenticationTokenFilter extends OncePerRequestFilter{
	
	@Autowired
	UserDetailsService userDetailsService;
	
	@Autowired
	JwtTokenUtil jwtTokenUtil;

	@Value("${jwt.header}")
	private String header;
	
	
	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {
	 String authToken=request.getHeader(header);
	 String username;
	
	 username=jwtTokenUtil.getUsernameFromToken(authToken);
	 
	 if(username!=null && SecurityContextHolder.getContext().getAuthentication()==null){
		
		 //can also be loaded from db
		 UserDetails userDetails=this.userDetailsService.loadUserByUsername(username);
		 if(jwtTokenUtil.validateToken(authToken, userDetails)){
			 
			 //not sure why this is authenticating with these arguments contrary to authentication controller
			 UsernamePasswordAuthenticationToken authentication=new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
			 authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
             SecurityContextHolder.getContext().setAuthentication(authentication);
			 
		 }
	 }
	 
	 chain.doFilter(request, response);	 
		
	}

}

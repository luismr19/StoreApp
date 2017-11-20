package com.pier.controllers.user;

import javax.servlet.http.HttpServletRequest;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@RestController
@EnableWebMvc
@RequestMapping(value="seo")
public class SearchEngineController {
	
	@RequestMapping(value="google/{entityType}/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> getGoogleResult(@PathVariable String entityType, @PathVariable Long id,HttpServletRequest request){
		
	}
	
	@RequestMapping(value="facebook/{entityType}/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> getFacebookResult(@PathVariable String entityType, @PathVariable Long id,HttpServletRequest request){
		
	}
	
	@RequestMapping(value="whatsapp/{entityType}/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> getWhatsAppResult(@PathVariable String entityType, @PathVariable Long id,HttpServletRequest request){
		
	}
	
	

}

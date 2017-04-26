package com.pier.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.UserIntegrityChecker;
import com.pier.model.security.User;
import com.pier.service.UserDao;

@RestController
public class ManageUserRestController {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserIntegrityChecker userCheker;
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users",method=RequestMethod.GET)
	public List<User> list(){
		
		return userDao.list();
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users/{id}",method=RequestMethod.GET)
	public ResponseEntity<User> showUser(@PathVariable("id") long id){
		
		return new ResponseEntity<User>(userDao.find(id),HttpStatus.FOUND);
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users/",method=RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user,UriComponentsBuilder ucBuilder){
		
		if(!userCheker.checkIfDuplicate(user) && userCheker.checkIfValid(user)){
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		userDao.add(user);		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        
		return new ResponseEntity<HttpHeaders>(headers,HttpStatus.CREATED);
		}else{
			return new ResponseEntity<List<String>>(userCheker.getErrors(),HttpStatus.FOUND);
		}
		
	}
	
	 @PreAuthorize("hasRole('ADMIN')")
	 @RequestMapping(value = "/user/{id}", method = RequestMethod.PUT)
	    public ResponseEntity<?> updateUser(@PathVariable("id") long id, @RequestBody User user) {
	        System.out.println("Updating User " + id);
	         
	        User currentUser = userDao.find(id);
	         
	        if (currentUser==null) {
	            System.out.println("User with id " + id + " not found");
	            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
	        }
	        if(userCheker.checkIfValid(user)){
	        currentUser.setFirstname(user.getFirstname());	        
	        currentUser.setLastname(user.getLastname());
	        currentUser.setEnabled(user.getEnabled());
	        currentUser.setPassword(passwordEncoder.encode(user.getPassword()));
	        currentUser.setAddress(user.getAddress());
	        currentUser.setEmail(user.getEmail());
	        currentUser.setAuthorities(user.getAuthorities());
	        
	        userDao.update(currentUser);
	        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	        }else{
	        	return new ResponseEntity<List<String>>(userCheker.getErrors(),HttpStatus.CONFLICT);
	        }  
	    }
	 
	 @PreAuthorize("hasRole('ADMIN')")
	 @RequestMapping(value = "/user/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<User> deleteUser(@PathVariable("id") long id) {
		 User user = userDao.find(id);
	        if (user == null) {
	            System.out.println("Unable to delete. User with id " + id + " not found");
	            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
	        }
	 
	        userDao.delete(user);
	        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	 }

}

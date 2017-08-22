package com.pier.controllers.admin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.UserIntegrityChecker;
import com.pier.model.security.Authority;
import com.pier.model.security.AuthorityName;
import com.pier.model.security.User;
import com.pier.rest.model.UserOrder;
import com.pier.service.AuthorityDao;
import com.pier.service.UserDao;

@RestController
@Transactional
public class ManageUserRestController {
	
	@Autowired
	UserDao userDao;
	
	@Autowired
	PasswordEncoder passwordEncoder;
	
	@Autowired
	UserIntegrityChecker userCheker;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	private AuthorityDao authDao;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(value="/users/ordes")
	public List<UserOrder> fetch(@RequestParam("index") int index, @RequestParam(value="filter",required=false)String word){
		int pageSize=30;
		
		Criteria criteria = currentSession().createCriteria(User.class);
		Disjunction or=Restrictions.disjunction();
		if(word!=null && !word.isEmpty()){
		or.add(Restrictions.like("username", "%"+word+"%"));
		or.add(Restrictions.like("firstname", "%"+word+"%"));
		or.add(Restrictions.like("lastname", "%"+word+"%"));
		}
		criteria.addOrder(Order.asc("firstname"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		List<User> users=criteria.list();
		
		return users.stream().map(user->new UserOrder(user,user.getOrders())).collect(Collectors.toList());
	}
	
	//@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users",method=RequestMethod.GET)
	public List<User> list(@RequestParam("index") int index){
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(User.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();
		
	}
	@RequestMapping(value="/users",params = {"filter","index"},method=RequestMethod.GET)
	public List<User> filter(@RequestParam("index") int index,@RequestParam("filter") String word){
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(User.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.like("username", "%"+word+"%"));
		or.add(Restrictions.like("firstname", "%"+word+"%"));
		criteria.addOrder(Order.asc("username"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users/{id}",method=RequestMethod.GET)
	public ResponseEntity<User> showUser(@PathVariable("id") long id){
		
		return new ResponseEntity<User>(userDao.find(id),HttpStatus.FOUND);
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users/orders/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> showUserOrder(@PathVariable("id") long id){
		User user=userDao.find(id);
		if(user!=null)
		return new ResponseEntity<UserOrder>(new UserOrder(user,user.getOrders()),HttpStatus.FOUND);
		
		return new ResponseEntity<String>("user not found",HttpStatus.NOT_FOUND);
		
	}
	
	
	@RequestMapping(value="/users/",method=RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user,UriComponentsBuilder ucBuilder){
		
		if(!userCheker.checkIfDuplicate(user) && userCheker.checkIfValid(user)){
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreatedDate(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		user.setAuthorities(authDao.find("AuthorityName", AuthorityName.ROLE_USER));
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

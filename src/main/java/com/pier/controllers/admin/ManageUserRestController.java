package com.pier.controllers.admin;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.hibernate.Criteria;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.query.Query;
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

import com.pier.business.util.PurchaseOrderComparator;
import com.pier.business.validation.UserIntegrityChecker;
import com.pier.model.security.Authority;
import com.pier.model.security.AuthorityName;
import com.pier.model.security.User;
import com.pier.rest.model.PurchaseOrder;
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
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/usersorders")
	public List<UserOrder> fetch(@RequestParam(value="index",required=false) Integer index, @RequestParam(value="filter",required=false)String word){
		index=(index==null)?0:index;
		int pageSize=30;
		
		Criteria criteria = currentSession().createCriteria(User.class);
		Disjunction or=Restrictions.disjunction();
		if(word!=null && !word.isEmpty()){
		or.add(Restrictions.ilike("username", "%"+word+"%"));
		or.add(Restrictions.ilike("firstname", "%"+word+"%"));
		or.add(Restrictions.ilike("lastname", "%"+word+"%"));
		}
		criteria.addOrder(Order.asc("firstname"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		List<User> users=criteria.list();
		
		return users.stream().map(user->new UserOrder(user,new ArrayList<PurchaseOrder>(user.getOrders()))).collect(Collectors.toList());
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users",method=RequestMethod.GET)
	public List<User> list(@RequestParam(value="index",required=false) Integer index){
		index=(index==null)?0:index;
		int pageSize=30;		
		Query<User> query=currentSession().createQuery("select distinct user from User user order by user.username asc");		
		query.setFirstResult(index).setMaxResults(pageSize);
		return query.getResultList();
		
	}
	@RequestMapping(value="/users",params = {"filter","index"},method=RequestMethod.GET)
	public List<User> filter(@RequestParam("index") int index,@RequestParam("filter") String word){
		int pageSize=30;
		Query<User> query=currentSession().createQuery("select distinct user from User user where user.username=:username or user.email=:email order by user.username asc");
		query.setParameter("username", word);
		query.setParameter("email", word);
		query.setFirstResult(index).setMaxResults(pageSize);		
		return query.getResultList();
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/users/{id}",method=RequestMethod.GET)
	public ResponseEntity<User> showUser(@PathVariable("id") long id){
		
		return new ResponseEntity<User>(userDao.find(id),HttpStatus.OK);
		
	}
	
	@SuppressWarnings("unused")
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="/usersorders/{id}",method=RequestMethod.GET)
	public ResponseEntity<?> showUserOrder(@PathVariable("id") long id){
		User user=userDao.find(id);
		Hibernate.initialize(user.getOrders());
		
		if(user!=null){
			List<PurchaseOrder> userOrders=new ArrayList<PurchaseOrder>(user.getOrders());
		Collections.sort(userOrders,Collections.reverseOrder(new PurchaseOrderComparator()));
		return new ResponseEntity<UserOrder>(new UserOrder(user,userOrders),HttpStatus.OK);
		}
		
		return new ResponseEntity<String>("user not found",HttpStatus.NOT_FOUND);
		
	}
	
	
	@RequestMapping(value="users",method=RequestMethod.POST)
	public ResponseEntity<?> createUser(@RequestBody User user,UriComponentsBuilder ucBuilder){
		
		if(!userCheker.checkIfDuplicate(user) && userCheker.checkIfValid(user)){
		user.setUsername(user.getUsername().toLowerCase());
		user.setEmail(user.getEmail().toLowerCase());
		user.setPassword(passwordEncoder.encode(user.getPassword()));
		user.setCreatedDate(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		user.setLastPasswordResetDate(LocalDateTime.now(ZoneId.of("America/Mexico_City")));
		user.setAuthorities(authDao.find("name", AuthorityName.ROLE_USER));
		user.setPhoneNumber(user.getPhoneNumber());
		userDao.add(user);		
		HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/user/{id}").buildAndExpand(user.getId()).toUri());
        
		return new ResponseEntity<HttpHeaders>(headers,HttpStatus.CREATED);
		}else{
			return new ResponseEntity<List<String>>(userCheker.getErrors(),HttpStatus.CONFLICT);
		}
		
	}
	
	 @PreAuthorize("hasRole('ADMIN')")
	 @RequestMapping(value = "/users/{id}", method = RequestMethod.PUT)
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
	        
	        userDao.update(currentUser);
	        return new ResponseEntity<User>(currentUser, HttpStatus.OK);
	        }else{
	        	return new ResponseEntity<List<String>>(userCheker.getErrors(),HttpStatus.CONFLICT);
	        }  
	    }
	 
	 @PreAuthorize("hasRole('ADMIN')")
	 @RequestMapping(value = "/users/{id}", method = RequestMethod.DELETE)
	    public ResponseEntity<?> deleteUser(@PathVariable("id") long id) {
		 User user = userDao.find(id);
	        if (user == null) {
	            System.out.println("Unable to delete. User with id " + id + " not found");
	            return new ResponseEntity<User>(HttpStatus.NOT_FOUND);
	        }
	 
	        userDao.delete(user);
	        return new ResponseEntity<User>(HttpStatus.NO_CONTENT);
	 }
	 
	 @RequestMapping(value = "/name/check", method = RequestMethod.GET)
	 public ResponseEntity<Boolean> checkExists(@RequestParam(value="field") String  value ){
		 Query query= userDao.currentSession().createQuery("from User where username=:username or email=:email");
		 query.setParameter("username", value);
		 query.setParameter("email", value);
		 
		 boolean exists=query.setMaxResults(1).uniqueResult() != null;
		 
		 return new ResponseEntity<Boolean>(exists,HttpStatus.OK);
		 
		 
	 }

}

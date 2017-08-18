package com.pier.controllers.admin;

import java.util.Collections;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.model.security.User;
import com.pier.rest.model.PurchaseOrder;
import com.pier.service.PurchaseOrderDao;
import com.pier.service.UserDao;

@RestController
@Transactional
@RequestMapping(value = "orders")
public class ManageOrdersRestController {

 @Autowired
 PurchaseOrderDao orderDao;
 
 @Autowired
 UserDao userDao;

 @Autowired
 private SessionFactory sessionFactory;

 private Session currentSession() {
  return sessionFactory.getCurrentSession();
 }

 @RequestMapping(method = RequestMethod.GET)
 public ResponseEntity < ? > getOrders(@RequestParam(value = "filter", required = false) String filter,
  @RequestParam("index") int index, @RequestParam(value = "order", required = false) String order) {
  int pageSize = 30;
  Criteria criteria = currentSession().createCriteria(PurchaseOrder.class);
  List < PurchaseOrder > results = Collections.emptyList();

  if(order==null)order=new String();
  
  order = (order.toLowerCase().equals("asc")) ? order : "desc";
 try{
  //standard search, returns all orders
  if (filter == null) {   
   criteria=getAllOrders(order,criteria);
  } else {
   //returns only concluded orders (you don't say!)
   if (filter.toLowerCase().equals("concluded")) {
	   criteria=getConcludedOrders(order,criteria);
   } else if (filter.toLowerCase().equals("shipped")) { //returns orders which have a tracking number
    //returns only shipped orders (you don't say!)
	 criteria=getShippedOrders(order,criteria);    
    }    
  }
  criteria.setFirstResult(index).setMaxResults(pageSize);
  results = criteria.list();
 }catch(Exception e){
	 return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR); 
 }

  return new ResponseEntity<List<PurchaseOrder>>(results,HttpStatus.OK);

 }
 
 @RequestMapping(value="/user/{id}",method=RequestMethod.GET)
 public ResponseEntity<?> getPurchaseOrdersByUser(@PathVariable long id){
	 int pageSize = 30;
	  Criteria criteria = currentSession().createCriteria(PurchaseOrder.class);
	  List < PurchaseOrder > results = Collections.emptyList();
	  criteria=getOrdersByUser("desc",criteria,id);
	  try{
	  results=criteria.list();
	  }catch(Exception e){
		  return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
	  }
	  return new ResponseEntity(results,HttpStatus.OK);
 }
 //useless as to now there is no way of modifying orders manually
 @RequestMapping(value="/{id}",method = RequestMethod.PUT)
 public ResponseEntity<?> modifyOrder(@PathVariable long id,@RequestBody PurchaseOrder order){
	 PurchaseOrder originalOrder=orderDao.find(id);
	 if(originalOrder!=null){
		 originalOrder.setConcluded(order.getConcluded());
		 originalOrder.setTrackingNumber(order.getTrackingNumber());
		 originalOrder.setDeliveryAddress(order.getDeliveryAddress());
		 originalOrder.setGift(order.getGift());		 
		 originalOrder.setOwner(order.getOwner());		 
		 originalOrder.setPurchaseItems(order.getPurchaseItems());
		 originalOrder.setTotal(order.getTotal());
		 
		 orderDao.update(originalOrder);			 
		 return new ResponseEntity<PurchaseOrder>(order,HttpStatus.OK);
	 }
	 
	 return new ResponseEntity<String>("order not found",HttpStatus.NOT_FOUND);	 
 }
 //useless as to now, there is no way of adding orders manually
 @RequestMapping(method = RequestMethod.POST)
 public ResponseEntity<?> createOrder(@RequestBody PurchaseOrder order){	
	 
	User user=userDao.find(order.getOwner().getId());
	if(user!=null){
	user.getOrders().add(order);
	userDao.update(user);
	
	}else{
		return new ResponseEntity<String>("user not found",HttpStatus.NOT_FOUND);		
	}
	return new ResponseEntity<PurchaseOrder>(order,HttpStatus.OK);		  
 }
 
 @RequestMapping(value="/{id}",method=RequestMethod.DELETE)
 public ResponseEntity<?> deleteOrder(Long id){
	 PurchaseOrder order=orderDao.find(id);
	 if(order!=null){
		 orderDao.delete(order);
		 return new ResponseEntity<String>("removed successfully",HttpStatus.NO_CONTENT);
	 }
	 
	 return new ResponseEntity<String>("order not found",HttpStatus.NOT_FOUND);
 }
 
private Criteria getAllOrders(String order, Criteria criteria){
	if (order.equals("desc"))
	    criteria.addOrder(Order.desc("total"));
	   else
	    criteria.addOrder(Order.asc("total"));	   
	   return criteria;	 
 }

private Criteria getConcludedOrders(String order, Criteria criteria){
	if (order.equals("desc"))
	    //check order
	     criteria.addOrder(Order.desc("purchaseDate"));
	    else
	     criteria.addOrder(Order.asc("purchaseDate"));

	    criteria.add(Restrictions.eq("concluded", true));	    
	  
	    return criteria;
 }

private Criteria getShippedOrders(String order,Criteria criteria){
	if (order.equals("desc"))
	     //check order
	      criteria.addOrder(Order.desc("purchaseDate"));
	     else
	      criteria.addOrder(Order.asc("purchaseDate"));

	     criteria.add(Restrictions.isNotNull("trackingNumber"));
	     
	     return criteria;
}

private Criteria getOrdersByUser(String order,Criteria criteria, Long userId){
	if (order.equals("desc"))
	     //check order
	      criteria.addOrder(Order.desc("purchaseDate"));
	     else
	      criteria.addOrder(Order.asc("purchaseDate"));

	     criteria.createAlias("owner", "ownr");
	     criteria.add(Restrictions.eq("ownr.id", userId));
	     
	     return criteria;
}
 

}
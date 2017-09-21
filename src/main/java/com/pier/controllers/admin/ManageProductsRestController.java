package com.pier.controllers.admin;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pier.business.validation.ProductIntegrityChecker;
import com.pier.rest.model.Flavor;
import com.pier.rest.model.Product;
import com.pier.service.ProductDao;
import com.pier.service.impl.FlavorService;

@RestController
@RequestMapping(value="products")
@Transactional
public class ManageProductsRestController {
	
	@Autowired
	ProductDao dao;
	
	@Autowired
	FlavorService flavorSvc;
	
	@Autowired
	ProductIntegrityChecker checker;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Value("${angular.images}")
	String assetsPaths;
	
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Product> list(@RequestParam(value="index",required=false) Integer index){
    index=(index==null)?0:index;
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(Product.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();
	}
	
	@RequestMapping(params = {"word","index"},method=RequestMethod.GET)
	public List<Product> filter(@RequestParam("index") int index,@RequestParam("filter") String word){
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(Product.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.ilike("name", "%"+word+"%"));
		or.add(Restrictions.ilike("description", "%"+word+"%"));
		criteria.addOrder(Order.asc("name"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();		
	}
	
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<Product> getProduct(@PathVariable Long id){
		
		Product product=dao.find(id);
		if(product!=null){
			return new ResponseEntity<Product>(product,HttpStatus.OK);
		}
		
		return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createProduct(@RequestBody Product product, UriComponentsBuilder ucBuilder){
		dao.currentSession().flush();
		dao.currentSession().clear();
		if(checker.checkIfDuplicate(product) || !checker.checkIfValid(product)){
			return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.CONFLICT);
		}
				
		//if a flavor doesn't exist yet, then a new one is generated		
		product.getFlavors().stream().filter(flav->flav.getId()==null).forEach(flav->flav.setId(flavorSvc.generateFlavor(flav.getFlavorName(), flav.getExistence()).getId()));
		
		if(product.getFlavors().isEmpty()){
			List<Flavor> flavors=new ArrayList<Flavor>();
			flavors.add(flavorSvc.generateFlavor("default",product.getExistence()));
			product.setFlavors(flavors);
		}
		
		dao.add(product);
		
		HttpHeaders headers = new HttpHeaders();
		headers.setLocation(ucBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri());
        
		return new ResponseEntity<Product>(product,headers,HttpStatus.CREATED);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> updateProduct(@PathVariable Long id, @RequestBody Product product){
		Product currentProduct=dao.find(id);
		dao.currentSession().flush();
		dao.currentSession().clear();
		if(currentProduct==null){
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}
		if(checker.checkIfValid(product)){
						
			if(product.getBrand()!=null){
			currentProduct.setBrand(product.getBrand());
			}
			if(product.getProductType()!=null){
				currentProduct.setProductType(product.getProductType());
			}
			if(product.getCategories()!=null){
				currentProduct.setCategories(product.getCategories());
			}
			if(product.getProductType()!=null){
					currentProduct.setProductType(product.getProductType());
			}
			
		try{
			//if a flavor doesn't exist yet, then a new one is generated		
			product.getFlavors().stream().filter(flav->flav.getId()==null).forEach(flav->flav.setId(flavorSvc.generateFlavor(flav.getFlavorName(), flav.getExistence()).getId()));
			
			if(product.getFlavors().isEmpty()){
				List<Flavor> flavors=new ArrayList<Flavor>();
				flavors.add(flavorSvc.generateFlavor("default",product.getExistence()));
				product.setFlavors(flavors);
			}
		}catch(NullPointerException ex){
			//do nothing, do not modify flavors
		}
			
			currentProduct.setName(product.getName());
			currentProduct.setDescription(product.getDescription());
			currentProduct.setEnabled(true);
			currentProduct.setPrice(product.getPrice());			
			
			dao.update(product);
			
			return new ResponseEntity<Product>(product,HttpStatus.OK);			
		}
		
		return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.NO_CONTENT);        
		
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="setprice/{id}",method=RequestMethod.PUT)
	public ResponseEntity<?> updatePrice(@PathVariable Long id, @RequestBody ObjectNode json){
		Product product=this.dao.find(id);
		try{
		String price=json.get("price").textValue();
		product.setPrice(new BigDecimal(price));
		dao.update(product);
		}catch(NullPointerException e){
			return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
		}
		return new ResponseEntity<Product>(product,HttpStatus.OK);
		
	
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity<?> handleFileUpload( @RequestPart("file") MultipartFile file, @RequestParam("id") Long id, HttpServletRequest request){
            
        if (!file.isEmpty()) {
            try {
                /*byte[] bytes = file.getBytes();
                BufferedOutputStream stream = 
                        new BufferedOutputStream(new FileOutputStream(new File(name + "-uploaded")));
                stream.write(bytes);
                stream.close();*/
                String uploadsDir = assetsPaths+"products/";
                //in case we need to change                
                if(! new File(uploadsDir).exists())
                {
                    new File(uploadsDir).mkdir();
                }
                String hql= "select max(id) from Product";
                List list = currentSession().createQuery(hql).list();
                long maxID = ( (Long)list.get(0) ).longValue();
                maxID++;
                if(id==null){
                	id=maxID;
                }
                
                String name = "prod_"+id+".jpg";
                String filePath = uploadsDir + name;
                File destination = new File(filePath);
                file.transferTo(destination);
                return new ResponseEntity<String>(""+id,HttpStatus.OK);
            } catch (Exception e) {
                
            	return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            
        	return new ResponseEntity<String>("file not valid",HttpStatus.BAD_REQUEST);
        }
    }
	

}

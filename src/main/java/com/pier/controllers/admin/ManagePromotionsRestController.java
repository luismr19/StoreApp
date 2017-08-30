package com.pier.controllers.admin;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Conjunction;
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

import com.pier.business.validation.PromotionIntegrityChecker;
import com.pier.rest.model.Promotion;
import com.pier.rest.model.PromotionRule;
import com.pier.service.PromotionDao;


@RestController
@RequestMapping(value = "promotions")
@Transactional
public class ManagePromotionsRestController {

	@Autowired
	PromotionDao dao;

	@Autowired
	PromotionIntegrityChecker checker;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	@Value("${angular.images}")
	String assetsPaths;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Promotion> list(@RequestParam(value="index",required=false) Integer index){
		index=(index==null)?0:index;
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(Promotion.class);
		criteria.addOrder(Order.desc("endDate"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();
	}
	@RequestMapping(params = {"word","index"},method=RequestMethod.GET)
	public List<Promotion> filter(@RequestParam("index") int index,@RequestParam("filter") String word,
			@RequestParam(value = "featured", defaultValue="n",required=false)String featured){
		int pageSize=30;
		Criteria criteria = currentSession().createCriteria(Promotion.class);
		Disjunction or=Restrictions.disjunction();
		or.add(Restrictions.ilike("displayName", "%"+word+"%"));
		or.add(Restrictions.ilike("description", "%"+word+"%"));		
		criteria.add(or);
        if(featured=="y"){
        	criteria.add(Restrictions.and(Restrictions.eq("featured", true)));
		}
		criteria.addOrder(Order.asc("displayName"));
		criteria.setFirstResult(index).setMaxResults(pageSize);
		return criteria.list();		
	}
	
	@RequestMapping("/active")
	public List<Promotion> getActive(){
		
		List<Promotion> results=Collections.EMPTY_LIST;
		
		Criteria criteria = currentSession().createCriteria(Promotion.class);
		Conjunction and=Restrictions.conjunction();
		and.add(Restrictions.le("startDate", LocalDateTime.now()));
		and.add(Restrictions.ge("endDate", LocalDateTime.now()));	
		and.add(Restrictions.eq("enabled", true));	
		criteria.add(and);
        
		criteria.addOrder(Order.asc("displayName"));
		try{
			results= criteria.list();
		}catch(Exception e){
			
		}
		return results;
	}
	
	@RequestMapping("/featured")
	public List<Promotion> getFeatured(){
		
		List<Promotion> results=Collections.EMPTY_LIST;
		
		Criteria criteria = currentSession().createCriteria(Promotion.class);
		Conjunction and=Restrictions.conjunction();
		and.add(Restrictions.le("startDate", LocalDateTime.now()));
		and.add(Restrictions.ge("endDate", LocalDateTime.now()));	
		and.add(Restrictions.eq("featured", true));	
		criteria.add(and);
        
		criteria.addOrder(Order.desc("endDate"));
		try{
			results= criteria.list();
		}catch(Exception e){
			
		}
		return results;
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPromotion(@PathVariable Long id) {
		Promotion promo = dao.find(id);
		if (promo != null) {
			return new ResponseEntity<Promotion>(promo, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);

	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createPromotion(@RequestBody Promotion promo, UriComponentsBuilder ucBuilder) {
		dao.currentSession().flush();
		dao.currentSession().clear();
		if (!checker.checkIfDuplicate(promo) && checker.checkIfValid(promo)) {
			dao.add(promo);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/promotion/{id}").buildAndExpand(promo.getId()).toUri());
			return new ResponseEntity<Promotion>(promo, headers, HttpStatus.OK);
		}
		return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.PUT, value = "{id}")
	public ResponseEntity<?> updatePromotion(@PathVariable Long id, @RequestBody Promotion promo) {
		dao.currentSession().flush();
		dao.currentSession().clear();
		Promotion currentPromo = dao.find(id);
		if (currentPromo != null) {
			if (checker.checkIfValid(promo)) {
				currentPromo.setDescription(promo.getDescription());
				currentPromo.setDisplayName(promo.getDisplayName());
				currentPromo.setEnabled(promo.getEnabled());
				currentPromo.setEndDate(promo.getEndDate());
				currentPromo.setStartDate(promo.getStartDate());
				currentPromo.setInclusive(promo.getInclusive());
				PromotionRule promoRule=promo.getPromotionRule();
				
				currentPromo.getPromotionRule().setBehavior(promoRule.getBehavior());
				currentPromo.getPromotionRule().setBrands(promoRule.getBrands());
				currentPromo.getPromotionRule().setCategories(promoRule.getCategories());
				currentPromo.getPromotionRule().setProductTypes(promoRule.getProductTypes());
				currentPromo.getPromotionRule().setProducts(promoRule.getProducts());
				currentPromo.getPromotionRule().setMinAmount(promoRule.getMinAmount());
				currentPromo.getPromotionRule().setMinPurchase(promoRule.getMinPurchase());
				currentPromo.getPromotionRule().setPercentage(promoRule.getPercentage());
				currentPromo.getPromotionRule().setPoints(promoRule.getPoints());
				currentPromo.getPromotionRule().setGiveAway(promoRule.getGiveAway());

				dao.update(currentPromo);
				return new ResponseEntity<Promotion>(currentPromo, HttpStatus.OK);
			}
			return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Promotion> delete(@PathVariable Long id) {
		Promotion promo = dao.find(id);

		if (promo != null) {
			dao.removePromotion(promo);
			return new ResponseEntity<Promotion>(promo, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Promotion>(HttpStatus.NOT_FOUND);

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
                String uploadsDir = assetsPaths;
                //in case we need to change                
                if(! new File(uploadsDir).exists())
                {
                    new File(uploadsDir).mkdir();
                }
                String hql= "select max(id) from Promotion";
                List list = currentSession().createQuery(hql).list();
                long maxID = ( (Long)list.get(0) ).longValue();
                maxID++;
                if(id==null || id==0){
                	id=maxID;
                }
                
                String name = Long.toString(id)+".jpg";
                String filePath = uploadsDir + name;
                File destination = new File(filePath);
                file.transferTo(destination);
                return new ResponseEntity<String>("success",HttpStatus.NO_CONTENT);
            } catch (Exception e) {
                
            	return new ResponseEntity<String>(e.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR);
            }
        } else {
            
        	return new ResponseEntity<String>("file not valid",HttpStatus.BAD_REQUEST);
        }
    }
}

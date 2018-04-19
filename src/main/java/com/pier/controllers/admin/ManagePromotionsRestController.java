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
import com.pier.service.impl.PromotionService;


@RestController
@RequestMapping(value = "promotions")
public class ManagePromotionsRestController {

	@Autowired
	PromotionService promotionSvc;	
	
	@Autowired
	PromotionIntegrityChecker checker;
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Promotion> list(@RequestParam(value="index",required=false) Integer index){
		return promotionSvc.list(index);
	}
	
	@RequestMapping(params = {"word","index"},method=RequestMethod.GET)
	public List<Promotion> filter(@RequestParam("index") int index,@RequestParam("filter") String word,
			@RequestParam(value = "featured", defaultValue="n",required=false)String featured){
		return promotionSvc.filter(index, word, featured);
	}
	
	@RequestMapping("/active")
	public List<Promotion> getActive(){
		return promotionSvc.getActiveVisible();
	}
	
	@RequestMapping("/featured")
	public List<Promotion> getFeatured(){
		return promotionSvc.getFeatured();
	}

	@RequestMapping(value = "{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getPromotion(@PathVariable Long id) {
		Promotion promo=promotionSvc.getPromotion(id);
		
		if (promo != null) {
			return new ResponseEntity<Promotion>(promo, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<?> createPromotion(@RequestBody Promotion promo, UriComponentsBuilder ucBuilder) {
		Promotion promotion=promotionSvc.createPromotion(promo);
		
		if (promotion!=null) {			
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/promotion/{id}").buildAndExpand(promo.getId()).toUri());
			return new ResponseEntity<Promotion>(promotion, headers, HttpStatus.OK);
		}
		
		boolean valid=checker.checkIfDuplicate(promo) && checker.checkIfValid(promo);
		return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
		
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.PUT, value = "{id}")
	public ResponseEntity<?> updatePromotion(@PathVariable Long id, @RequestBody Promotion promo) {
		Promotion promotion=promotionSvc.updatePromotion(id, promo);
		if(promotion!=null) {
			if(promotion.getId()!=null) {
			return new ResponseEntity<Promotion>(promotion, HttpStatus.OK);
			}else {
				return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);	
			}
		}else{	
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);	
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Promotion> delete(@PathVariable Long id) {
		Promotion promo= promotionSvc.delete(id);
		if(promo!=null)
			return new ResponseEntity<Promotion>(promo, HttpStatus.NO_CONTENT);
		else
			return new ResponseEntity<Promotion>(HttpStatus.NOT_FOUND);
	
	}
	
	@RequestMapping(value="/upload", method=RequestMethod.POST)
    public ResponseEntity<?> handleFileUpload( @RequestPart("file") MultipartFile file, @RequestParam("id") Long id, HttpServletRequest request){
		return promotionSvc.handleFileUpload(file, id, request);    
    }
}

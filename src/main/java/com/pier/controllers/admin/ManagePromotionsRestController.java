package com.pier.controllers.admin;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.PromotionIntegrityChecker;
import com.pier.rest.model.Promotion;
import com.pier.service.PromotionDao;

@RestController
@RequestMapping(value = "promotions")
public class ManagePromotionsRestController {

	@Autowired
	PromotionDao dao;

	@Autowired
	PromotionIntegrityChecker checker;

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
		if (!checker.checkIfDuplicate(promo) && checker.checkIfValid(promo)) {
			dao.add(promo);
			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/articles/{id}").buildAndExpand(promo.getId()).toUri());
			return new ResponseEntity<Promotion>(promo, headers, HttpStatus.CREATED);
		}
		return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.PUT, value = "{id}")
	public ResponseEntity<?> updatePromotion(@PathVariable Long id, @RequestBody Promotion promo) {
		Promotion currentPromo = dao.find(id);
		if (currentPromo != null) {
			if (checker.checkIfValid(promo)) {
				currentPromo.setDescription(promo.getDescription());
				currentPromo.setDisplayName(promo.getDisplayName());
				currentPromo.setEnabled(promo.getEnabled());
				currentPromo.setEndDate(promo.getEndDate());
				currentPromo.setStartDate(promo.getStartDate());
				currentPromo.setInclusive(promo.getInclusive());
				currentPromo.setPromotionrule(promo.getPromotionRule());

				dao.update(currentPromo);
				return new ResponseEntity<Promotion>(currentPromo, HttpStatus.FOUND);
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
}

package com.pier.business.validation;

import java.time.ZonedDateTime;

import org.springframework.stereotype.Component;

import com.pier.rest.model.Promotion;
import com.pier.service.PromotionDao;

@Component
public class PromotionIntegrityChecker extends IntegrityCheckerImpl<Promotion, PromotionDao> {

	@Override
	public boolean checkIfDuplicate(Promotion model) {
		errors.clear();
		boolean result=dao.find("displayName", model.getDisplayName())
				.stream().anyMatch(p->p.equals(model));
		if(result){
			errors.add("Promotion already exists, check your values");
		}
		return result;
	}

	@Override
	public boolean checkIfValid(Promotion model) {
		errors.clear();
		boolean result=checkDates(model);
		
		return result;
	}

	private boolean checkDates(Promotion promo) {
		
		boolean result=promo.getStartDate().isBefore(ZonedDateTime.now());
		result=result || promo.getEndDate().isBefore(ZonedDateTime.now());
		if(result){
			errors.add("cannot set date before today");
		}
		
		return result;
		
		
	}

	
}

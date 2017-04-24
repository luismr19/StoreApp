package com.pier.business.validation;


import org.apache.commons.validator.routines.UrlValidator;
import org.springframework.stereotype.Component;

import com.pier.rest.model.Article;
import com.pier.service.ArticleDao;

@Component
public class ArticleIntegrityChecker extends IntegrityCheckerImpl<Article,ArticleDao>{
	
	@Override
	public boolean checkIfValid(Article article){
		errors.clear();
		UrlValidator validator= new UrlValidator();
		boolean valid=validator.isValid(article.getLink());
		if(!valid){
		errors.add("Url is not valid");
		}
		return valid;
	}
	
	@Override
	public boolean checkIfDuplicate(Article article){
		errors.clear();
		boolean result=dao.find("link", article.getLink()).size()>0;
		if(result)
			errors.add("Article already exists or a resource already exists under that URL");
		return result;
	}

}

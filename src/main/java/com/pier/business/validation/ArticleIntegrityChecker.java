package com.pier.business.validation;


import org.springframework.stereotype.Component;

import com.pier.rest.model.Article;
import com.pier.service.ArticleDao;

@Component
public class ArticleIntegrityChecker extends IntegrityCheckerImpl<Article,ArticleDao>{
	
	@Override
	public boolean checkIfValid(Article article){
		errors.clear();
		//UrlValidator validator= new UrlValidator();
		boolean valid=true;
		if(!valid){
		errors.add("article is not valid");
		}
		return valid;
	}
	
	@Override
	public boolean checkIfDuplicate(Article article){
		errors.clear();
		boolean result=dao.find("title", article.getTitle()).size()>0;
		if(result)
			errors.add("Article already exists or a resource already exists under that name");
		return result;
	}
	
	
	public boolean checkNotOwner(String username, Article article){
		errors.clear();
		Article result=dao.find("title", article.getTitle()).get(0);
		if(result!=null && !result.getAutor().getUsername().equals(username)){
			errors.add("Article already exists or a resource already exists under that name");
			return true;
		}
		return false;
	}

}

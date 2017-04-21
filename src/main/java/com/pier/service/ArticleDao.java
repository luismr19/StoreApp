package com.pier.service;

import com.pier.rest.model.Article;

public interface ArticleDao extends GenericDao<Article, Long> {
	
	boolean removeArticle(Article article);

}

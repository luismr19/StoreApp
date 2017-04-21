package com.pier.service.impl;

import com.pier.service.ArticleDao;

import org.springframework.stereotype.Repository;

import com.pier.rest.model.Article;

@Repository("articleDao")
public class ArticleDaoImpl extends HibernateDao<Article,Long> implements ArticleDao{

	@Override
	public boolean removeArticle(Article article) {
		delete(article);
		return true;
	}

}

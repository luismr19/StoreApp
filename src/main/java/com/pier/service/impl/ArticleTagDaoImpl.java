package com.pier.service.impl;

import com.pier.rest.model.ArticleTag;
import com.pier.service.ArticleDao;
import com.pier.service.ArticleTagDao;

import org.springframework.stereotype.Repository;

@Repository("articleTagDao")
public class ArticleTagDaoImpl extends HibernateDao<ArticleTag,Long> implements ArticleTagDao{

	@Override
	public boolean removeArticleTag(ArticleTag tag) {
		delete(tag);
		return true;
	}

}

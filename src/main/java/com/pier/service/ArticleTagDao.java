package com.pier.service;

import com.pier.rest.model.ArticleTag;

public interface ArticleTagDao extends GenericDao<ArticleTag, Long> {
	
	boolean removeArticleTag(ArticleTag tag);

}

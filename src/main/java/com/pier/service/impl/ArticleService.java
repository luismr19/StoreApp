package com.pier.service.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;

import com.pier.model.security.User;
import com.pier.rest.model.Article;
import com.pier.rest.model.ArticleTag;
import com.pier.service.ArticleDao;

@Transactional
@Component
public class ArticleService {
	
	@Autowired
	private ArticleDao dao;
	
	
public List<ArticleTag> handleTags(List<ArticleTag> tags){
		
		List<ArticleTag> results=new ArrayList<ArticleTag>();
		
		List<String> tagNames=tags.stream().map(tag->tag.getName()).collect(Collectors.toList());
		
		Query<ArticleTag> findTags=dao.currentSession().createQuery("FROM ArticleTag tag where tag.name in (:names)");
		
		findTags.setParameterList("names", tagNames);
		
		List<ArticleTag> existingTags=findTags.getResultList();
		
		List<String> existingTagNames=existingTags.stream().map(tag->tag.getName()).collect(Collectors.toList());	
		
		
		Collection<String> newTags=CollectionUtils.subtract(tagNames,existingTagNames);
		
		for(String tag:newTags){
			ArticleTag newTag=new ArticleTag();
			newTag.setName(tag);
			results.add(newTag);
		}
		
		results.addAll(existingTags);
		
		return results;
		
	}

public List<Article> getArticles(int index, String filter,String order,String from,String to,Long userId){
	int pageSize = 30;
	// this is using the one and only ugly criteria builder
	CriteriaBuilder criteriaBuilder = dao.currentSession().getCriteriaBuilder();
	CriteriaQuery<Article> articleQuery = criteriaBuilder.createQuery(Article.class);

	LocalDateTime fromDate = LocalDateTime.now().minusMonths(2);
	LocalDateTime toDate = LocalDateTime.now();

	order = (order != null) ? "desc" : "asc";

	List<Predicate> conditionsList = new ArrayList<Predicate>();

	Root<Article> rootArticle = articleQuery.from(Article.class);

	if (from != null) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		fromDate = LocalDate.parse(from, formatter).atStartOfDay();
	}

	if (to != null) {
		final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		toDate = LocalDate.parse(to, formatter).atStartOfDay();
	}

	Predicate afterDate = criteriaBuilder.greaterThanOrEqualTo(rootArticle.get("writeDate"), fromDate);
	conditionsList.add(afterDate);
	Predicate beforeDate = criteriaBuilder.greaterThanOrEqualTo(rootArticle.get("writeDate"), toDate);
	conditionsList.add(beforeDate);

	if (filter.equals("enabled")) {
		Predicate enabled = criteriaBuilder.equal(rootArticle.get("enabled"), true);
		conditionsList.add(enabled);
	} else if (filter.equals("featured")) {
		Predicate featured = criteriaBuilder.equal(rootArticle.get("featured"), true);
		conditionsList.add(featured);
	}

	if (userId != null && userId > 0) {
		Join<Article, User> articleAutor = rootArticle.join("autor");
		Predicate autorEquals = criteriaBuilder.equal(articleAutor.get("id"), userId);
		// Predicate autorEquals =
		// criteriaBuilder.equal(rootArticle.get("owner").get("id"),
		// userId);
		conditionsList.add(autorEquals);

	}
	// always show featured first I guess
	javax.persistence.criteria.Order featuredOrder = criteriaBuilder.desc(rootArticle.get("featured"));

	if (order.equals("asc"))
		articleQuery.orderBy(featuredOrder, criteriaBuilder.asc(rootArticle.get("writeDate")));
	else
		articleQuery.orderBy(featuredOrder, criteriaBuilder.desc(rootArticle.get("writeDate")));

	List<Article> results = dao.currentSession()
			.createQuery(articleQuery.select(rootArticle).where(conditionsList.toArray(new Predicate[] {})))
			.setFirstResult(index).setMaxResults(pageSize).getResultList();

	return results;
}

}

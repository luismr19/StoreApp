package com.pier.controllers.user;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import org.hibernate.Criteria;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.pier.rest.model.Article;
import com.pier.service.ArticleDao;
import com.pier.service.impl.UserService;

public class ArticleDashboardRestController {
	
	@Autowired
	UserService userService;
	
	@Value("${angular.articles}")
	String articlesPaths;
	
	@Autowired
	private ArticleDao dao;
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	
	@RequestMapping(value="articles",method = RequestMethod.GET)
	public List<Article> list() {

		Criteria criteria = dao.currentSession().createCriteria(Article.class);
		criteria.addOrder(Order.desc("id"));
		criteria.setFirstResult(0).setMaxResults(30);
		return criteria.list();
	}	
	
	@RequestMapping(value="articles/list", params = { "from", "to" }, method = RequestMethod.GET)
	public ResponseEntity<?> getArticles(
			  @RequestParam("index") int index,
			  @RequestParam(value = "order", required = false) String order,
			  @RequestParam(value="from", required=false) String from,			  
			  @RequestParam(value="to", required=false) String to  ) {
		 int pageSize = 30;
		 //this is using the one and only ugly criteria builder 
		 CriteriaBuilder criteriaBuilder = dao.currentSession().getCriteriaBuilder();
		 CriteriaQuery<Article> articleQuery=criteriaBuilder.createQuery(Article.class);
		 
		 LocalDateTime fromDate;
		 LocalDateTime toDate=LocalDateTime.now();
		 
		 
		 List<Predicate> conditionsList = new ArrayList<Predicate>();
		 
		 Root<Article> rootArticle=articleQuery.from(Article.class);
		 
		 
		 if(from!=null){
			 final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
			 fromDate= LocalDate.parse(from, formatter).atStartOfDay();			 
			 Predicate afterDate=criteriaBuilder.greaterThanOrEqualTo(rootArticle.get("writeDate"), fromDate);
			 conditionsList.add(afterDate);			 
		 }
		 
		 Predicate beforeDate= criteriaBuilder.lessThanOrEqualTo(rootArticle.get("writeDate"), toDate);
		 conditionsList.add(beforeDate);
		 
		 
		 Predicate enabled=criteriaBuilder.equal(rootArticle.get("enabled"), true);
		 conditionsList.add(enabled); 
		 
		
		 
		 articleQuery.orderBy(criteriaBuilder.desc(rootArticle.get("featured")),
				 criteriaBuilder.desc(rootArticle.get("writeDate")));
		 
		 
		 List<Article> results=dao.currentSession().createQuery(articleQuery.select(rootArticle)
				 .where(conditionsList.toArray(new Predicate[]{}))).setFirstResult(index).setMaxResults(pageSize).getResultList();
		 
		 return new ResponseEntity<List<Article>>(results,HttpStatus.OK);
	 
	 }
	
	@RequestMapping(value="articles/find", params = { "word", "index" }, method = RequestMethod.GET)
	public List<Article> filter(@RequestParam("index") int index, @RequestParam("filter") String word) {
		Criteria criteria = dao.currentSession().createCriteria(Article.class);

		criteria.add(Restrictions.ilike("title", "%" + word + "%"));
		criteria.addOrder(Order.asc("title"));
		criteria.setFirstResult(index).setMaxResults(30);
		return criteria.list();
	}
	
	
	@RequestMapping(value = "article/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getArticle(@PathVariable Long id) {
		Article article = dao.find(id);
		if (article != null) {
			return new ResponseEntity<Article>(article, HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}

}

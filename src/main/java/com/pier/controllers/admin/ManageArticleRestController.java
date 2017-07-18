package com.pier.controllers.admin;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

import com.pier.business.validation.ArticleIntegrityChecker;
import com.pier.rest.model.Article;
import com.pier.service.ArticleDao;

@RestController
@RequestMapping(value="articles")
public class ManageArticleRestController {
	
	@Autowired
	private ArticleDao dao;
	
	@Autowired
	ArticleIntegrityChecker checker;
	
	@Autowired
	private SessionFactory sessionFactory;
	
	private Session currentSession(){
		return sessionFactory.getCurrentSession();
	}
	
	@RequestMapping(method=RequestMethod.GET)
	public List<Article> list(){
		
		Criteria criteria = currentSession().createCriteria(Article.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(0).setMaxResults(2);
		return criteria.list();
	}
	
	@RequestMapping(params = {"word","index"},method=RequestMethod.GET)
	public List<Article> filter(@RequestParam("index") int index,@RequestParam("filter") String word){
		Criteria criteria = currentSession().createCriteria(Article.class);
		
		criteria.add(Restrictions.like("title", "%"+word+"%"));		
		criteria.addOrder(Order.asc("title"));
		criteria.setFirstResult(index).setMaxResults(50);
		return criteria.list();		
	}
	
	@RequestMapping(value="{id}",method=RequestMethod.GET)
	public ResponseEntity<?> getArticle(@PathVariable Long id){
		Article article=dao.find(id);
		if(article!=null){
			return new ResponseEntity<Article>(article,HttpStatus.OK);
		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.POST)
	public ResponseEntity<?> createArticle(@RequestBody Article article,UriComponentsBuilder ucBuilder){
		
		if(checker.checkIfValid(article)&& !checker.checkIfDuplicate(article)){
			dao.add(article);
			
			HttpHeaders headers = new HttpHeaders();
	        headers.setLocation(ucBuilder.path("/articles/{id}").buildAndExpand(article.getId()).toUri());
	        return new ResponseEntity<Article>(article,headers,HttpStatus.CREATED);
		}
		
		return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.CONFLICT);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method=RequestMethod.PUT,value="{id}")
	public ResponseEntity<?> updateArticle(@PathVariable Long id,@RequestBody Article article){
		
		Article currentArticle=dao.find(id);
		if(article!=null){ 
		if(checker.checkIfValid(article)){
			
			currentArticle.setLink(article.getLink());
			dao.update(currentArticle);
			       
	        return new ResponseEntity<Article>(article,HttpStatus.FOUND);
		}
		
		return new ResponseEntity<List<String>>(checker.getErrors(),HttpStatus.CONFLICT);
	} 
		return new ResponseEntity<Article>(article,HttpStatus.NOT_FOUND);
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value="{id}",method=RequestMethod.DELETE)
	public ResponseEntity<Article> deleteArticle(@PathVariable Long id){
		
		Article article=dao.find(id);		
		if(article!=null){
			dao.removeArticle(article);
			return new ResponseEntity<Article>(article,HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Article>(HttpStatus.NOT_FOUND);
	}

}

package com.pier.controllers.admin;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pier.business.validation.ArticleIntegrityChecker;
import com.pier.config.SpecObjectMapper;
import com.pier.model.security.User;
import com.pier.rest.model.Article;
import com.pier.rest.model.ArticleTag;
import com.pier.service.ArticleDao;
import com.pier.service.impl.UserService;

@RestController
@RequestMapping(value = "articles")
public class ManageArticleRestController {

	@Autowired
	private ArticleDao dao;

	@Autowired
	ArticleIntegrityChecker checker;

	@Autowired
	private SessionFactory sessionFactory;
	
	@Autowired
	SpecObjectMapper objectMapper;
	
	@Autowired
	UserService userService;
	
	@Value("${angular.articles}")
	String articlesPaths;
	
	@Value("${jwt.header}")
	private String tokenHeader;

	private Session currentSession() {
		return dao.currentSession();
	}

	@RequestMapping(value="article",method = RequestMethod.GET)
	public List<Article> list() {

		Criteria criteria = currentSession().createCriteria(Article.class);
		criteria.addOrder(Order.asc("id"));
		criteria.setFirstResult(0).setMaxResults(30);
		return criteria.list();
	}
	
	 

	@RequestMapping(value="article", params = { "word", "index" }, method = RequestMethod.GET)
	public List<Article> filter(@RequestParam("index") int index, @RequestParam("filter") String word) {
		Criteria criteria = currentSession().createCriteria(Article.class);

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

	@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
	@RequestMapping(value="article", method = RequestMethod.POST)
	public ResponseEntity<?> createArticle(@RequestBody Article article, UriComponentsBuilder ucBuilder) {
		
		article.setEnabled(false);
		if (checker.checkIfValid(article) && !checker.checkIfDuplicate(article)) {
			dao.add(article);

			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/articles/{id}").buildAndExpand(article.getId()).toUri());
			return new ResponseEntity<Article>(article, headers, HttpStatus.CREATED);
		}

		return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(method = RequestMethod.PUT, value = "article/{id}")
	public ResponseEntity<?> updateArticle(@PathVariable Long id, @RequestBody Article article) {

		Article currentArticle = dao.find(id);
		if (article != null) {
			if (checker.checkIfValid(article)) {

				currentArticle.setLink(article.getLink());
				currentArticle.setEnabled(article.getEnabled());
				currentArticle.setLastEdited(LocalDateTime.now());
				currentArticle.setTags(article.getTags());
				currentArticle.setTitle(article.getTitle());
				dao.update(currentArticle);

				return new ResponseEntity<Article>(article, HttpStatus.OK);
			}

			return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
		}
		return new ResponseEntity<Article>(article, HttpStatus.NOT_FOUND);
	}
	
	public ResponseEntity < ? > getArticles(@RequestParam(value = "filter", required = false) String filter,
			  @RequestParam("index") int index, @RequestParam(value = "order", required = false) String order,
			  @RequestParam(value="from", required=false) String from,			  
			  @RequestParam(value="to", required=false) String to,
			  @RequestParam(value="user", required = false) Long userId) {
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
		 
		 if(filter.equals("enabled")){
			 Predicate enabled=criteriaBuilder.equal(rootArticle.get("enabled"), true);
			 conditionsList.add(enabled);
		 }else if(filter.equals("featured")){
			 Predicate featured=criteriaBuilder.equal(rootArticle.get("featured"), true);
			 conditionsList.add(featured); 
		 }
		 
		 if(userId!=null && userId>0){
			Join<Article,User> articleAutor=rootArticle.join("owner"); 
			Predicate autorEquals = criteriaBuilder.equal(articleAutor.get("id"), userId);
			//Predicate autorEquals = criteriaBuilder.equal(rootArticle.get("owner").get("id"), userId);
			conditionsList.add(autorEquals);
			
		 }		 
		 
		 List<Article> results=dao.currentSession().createQuery(articleQuery.select(rootArticle)
				 .where(conditionsList.toArray(new Predicate[]{}))).getResultList();
		 
		 return new ResponseEntity<List<Article>>(results,HttpStatus.OK);
	 
	 }
	
	@PreAuthorize("hasRole(EDITOR)")
	@RequestMapping(value="publish", method=RequestMethod.POST)
	public ResponseEntity<?> publishArticle(@RequestBody ObjectNode articleBody, HttpServletRequest request, UriComponentsBuilder ucBuilder){
		
		String filePath=articlesPaths+ articleBody.get("title").textValue().replaceAll(" ", "_")+".json";
		File jsonFile = new File(filePath);
		
		String token=request.getHeader(tokenHeader);
		
		if(!jsonFile.exists()) { 
			try {
				//write the json into a file
				objectMapper.writeValue(jsonFile,articleBody);
				
				JsonNode articleTagsNode=articleBody.get("tags");
				
				List<ArticleTag> articleTags=objectMapper.readValue(articleTagsNode.traverse(), new TypeReference<ArrayList<ArticleTag>>(){});
				
				Article newArticle=new Article();
				newArticle.setTitle(articleBody.get("title").textValue());
				newArticle.setEnabled(false);
				newArticle.setAutor(userService.getUserFromToken(token));
				newArticle.setWriteDate(LocalDateTime.now());
				newArticle.setLink(filePath);
				
				newArticle.setTags(new HashSet<ArticleTag>(articleTags));
				
				if (checker.checkIfValid(newArticle) && !checker.checkIfDuplicate(newArticle)) {
					dao.add(newArticle);

					HttpHeaders headers = new HttpHeaders();
					headers.setLocation(ucBuilder.path("/articles/{id}").buildAndExpand(newArticle.getId()).toUri());
					return new ResponseEntity<Article>(newArticle, headers, HttpStatus.CREATED);
				}
				
				return new ResponseEntity<String>("Title already exists",HttpStatus.CONFLICT);
			} catch (IOException e) {
				if(jsonFile.exists()) {
					if(!jsonFile.delete()){
						return new ResponseEntity<String>("there was an error saving the article,"
								+ "the file needs to be removed manually",HttpStatus.INTERNAL_SERVER_ERROR);	
					}
				}
				return new ResponseEntity<String>("Error writing file",HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		
		return new ResponseEntity<String>("Title already exists",HttpStatus.CONFLICT);		
		
	}
	

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Article> deleteArticle(@PathVariable Long id) {

		Article article = dao.find(id);
		if (article != null) {
			dao.removeArticle(article);
			return new ResponseEntity<Article>(article, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Article>(HttpStatus.NOT_FOUND);
	}

}

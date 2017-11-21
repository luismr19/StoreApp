package com.pier.controllers.user;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pier.config.Crawlers;
import com.pier.config.SpecObjectMapper;
import com.pier.rest.model.Article;
import com.pier.rest.model.ArticleTag;
import com.pier.service.ArticleDao;
import com.pier.service.impl.ArticleService;
import com.pier.service.impl.UserService;

@RestController
@RequestMapping(value=Crawlers.ENTITY_ARTICLES)
public class ArticleDashboardRestController {
	
	@Autowired
	UserService userService;
	
	@Value("${angular.articles}")
	String articlesPaths;
	
	@Autowired
	private ArticleDao dao;
	
	@Value("${jwt.header}")
	private String tokenHeader;
	
	@Autowired
	SpecObjectMapper objectMapper;
	
	@Autowired
	ArticleService articleService;
	
	
	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<?> getArticles(
			  @RequestParam("index") int index,			  
			  @RequestParam(value="tag",required=false) List<Long> tags) {
		
		List<Article> articles=articleService.getPublicArticles(index, tags); 
		 return new ResponseEntity<List<Article>>(articles,HttpStatus.OK);
	 
	 }
	
	@RequestMapping(value="find", params = { "filter", "index" }, method = RequestMethod.GET)
	public List<Article> filter(@RequestParam("index") int index, @RequestParam("filter") String word, @RequestParam(value="tag",required=false) List<Long> tags) {
		if(tags!=null && !tags.isEmpty())
			return articleService.findArticles(index, word, tags);			
		else
			return articleService.findArticles(index,word);
		
			
	}
	
	@RequestMapping(value="tags", params = { "filter"}, method = RequestMethod.GET)
	public List<ArticleTag> findTags(@RequestParam("filter") String word) {
		return articleService.findTags(word);
	}
	
	
	
	@RequestMapping(value = "/{id}", method = RequestMethod.GET)
	public ResponseEntity<?> getArticle(@PathVariable Long id) {
		Article article = dao.find(id);
		if (article != null) {
			ResponseEntity respEntity = null;

			byte[] reportBytes = null;
			String fileName=getArticleFile(article);
			File result = new File(fileName);

			ObjectNode articleBody = objectMapper.createObjectNode();
			articleBody = objectMapper.createObjectNode();

			if (result.exists()) {
				InputStream inputStream;

				String type;
				try {
					inputStream = new FileInputStream(result);					

					articleBody.putPOJO("article", article);
					articleBody.set("body", objectMapper.readTree(inputStream));

					respEntity = new ResponseEntity(articleBody, HttpStatus.OK);
				} catch (IOException e) {
					return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
				}

			} else {
				articleBody.putPOJO("article", article);
				articleBody.putObject("body");
				respEntity = new ResponseEntity(articleBody, HttpStatus.OK);
			}
			return respEntity;

		}
		return new ResponseEntity<Void>(HttpStatus.NOT_FOUND);
	}
	
	private String getArticleFile(Article article){
		return this.articlesPaths + article.getId() + "_" + article.getTitle().replaceAll(" ", "_")+ ".json";
	}

}

package com.pier.controllers.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.query.Query;
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
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.pier.business.validation.ArticleIntegrityChecker;
import com.pier.config.SpecObjectMapper;
import com.pier.model.security.User;
import com.pier.rest.model.Article;
import com.pier.rest.model.ArticleTag;
import com.pier.security.JwtUser;
import com.pier.service.ArticleDao;
import com.pier.service.impl.ArticleService;
import com.pier.service.impl.UserService;

@RestController
public class ManageArticleRestController {

	@Autowired
	private ArticleDao dao;

	@Autowired
	ArticleIntegrityChecker checker;

	@Autowired
	ArticleService articleService;

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

	@RequestMapping(value = "article", method = RequestMethod.GET)
	public List<Article> list(@RequestParam(value = "index", required = false) int index) {

		Criteria criteria = currentSession().createCriteria(Article.class);
		criteria.addOrder(Order.desc("id"));
		criteria.setFirstResult(index).setMaxResults(30);
		return criteria.list();
	}

	@RequestMapping(value = "article/mine", method = RequestMethod.GET)
	public Set<Article> myArticles(HttpServletRequest request) {

		String token = request.getHeader(tokenHeader);

		User user = userService.getUserFromTokenWithArticles(token);

		return user.getArticles();
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
	@RequestMapping(value = "article/{id}", method = RequestMethod.GET)
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

	@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
	@RequestMapping(value = "article", method = RequestMethod.POST)
	public ResponseEntity<?> newArticle(@RequestBody ObjectNode body, UriComponentsBuilder ucBuilder,
			HttpServletRequest request) {

		Article article = new Article();
		String token = request.getHeader(tokenHeader);
		User user = userService.getUserFromToken(token);
		article.setTitle(body.get("title").textValue());
		article.setAutor(user);
		article.setEnabled(false);
		article.setWriteDate(LocalDateTime.now());
		article.setLastEdited(LocalDateTime.now());

		if (checker.checkIfValid(article) && !checker.checkIfDuplicate(article)) {
			dao.add(article);

			HttpHeaders headers = new HttpHeaders();
			headers.setLocation(ucBuilder.path("/articles/{id}").buildAndExpand(article.getId()).toUri());
			return new ResponseEntity<Article>(article, headers, HttpStatus.CREATED);
		}

		return new ResponseEntity<List<String>>(checker.getErrors(), HttpStatus.CONFLICT);
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "article/enable/{id}", method = RequestMethod.PUT)
	public ResponseEntity<?> enableArticle(@PathVariable Long id) {

		Article currentArticle = dao.find(id);
		if (currentArticle == null)
			return new ResponseEntity<String>("article not found", HttpStatus.NOT_FOUND);
		currentArticle.setEnabled(true);
		dao.update(currentArticle);

		return new ResponseEntity<Article>(currentArticle, HttpStatus.OK);
	}

	@PreAuthorize("hasRole('ADMIN') or hasRole('EDITOR')")
	@RequestMapping(value = "article/uploadimg", method = RequestMethod.POST)
	public ResponseEntity<?> handleFileUpload(@RequestPart("file") MultipartFile file, @RequestParam("id") Long id,
			HttpServletRequest request, @RequestParam(value = "thumbnail", required = false) boolean thumbnail) {

		String token = request.getHeader(tokenHeader);
		JwtUser user = userService.getJwtUserFromToken(token);

		if (!dao.find(id).getAutor().getUsername().equals(user.getUsername())) {
			List<String> authorities = user.getAuthorities().stream().map(auth -> auth.getAuthority())
					.collect(Collectors.toList());
			if (!authorities.contains("ROLE_ADMIN"))
				return new ResponseEntity<String>("not authorized", HttpStatus.UNAUTHORIZED);
		}

		if (!file.isEmpty()) {
			try {
				/*
				 * byte[] bytes = file.getBytes(); BufferedOutputStream stream =
				 * new BufferedOutputStream(new FileOutputStream(new File(name +
				 * "-uploaded"))); stream.write(bytes); stream.close();
				 */
				String uploadsDir = this.articlesPaths + "images/";
				// in case we need to change
				if (!new File(uploadsDir).exists()) {
					new File(uploadsDir).mkdir();
				}

				String name = "";
				if (!thumbnail)
					name = Long.toString(id) + "_" + file.getOriginalFilename() + ".jpg";
				else
					name = "art" + Long.toString(id) + ".jpg";
				String filePath = uploadsDir + name;
				File destination = new File(filePath);
				file.transferTo(destination);
				return new ResponseEntity<String>("" + id, HttpStatus.OK);
			} catch (Exception e) {

				return new ResponseEntity<String>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
			}
		} else {

			return new ResponseEntity<String>("file not valid", HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "articles/list", method = RequestMethod.GET)
	public ResponseEntity<?> getArticles(@RequestParam("index") int index,
			@RequestParam(value = "filter", required = false) String filter,
			@RequestParam(value = "order", required = false) String order,
			@RequestParam(value = "from", required = false) String from,
			@RequestParam(value = "to", required = false) String to,
			@RequestParam(value = "user", required = false) Long userId) {

		List<Article> articles = articleService.getArticles(index, filter, order, from, to, userId);
		return new ResponseEntity<List<Article>>(articles, HttpStatus.OK);

	}

	@PreAuthorize("hasRole('EDITOR') or hasRole('ADMIN')")
	@RequestMapping(value = "article/publish/{id}", method = RequestMethod.POST)
	public ResponseEntity<?> publishArticle(@RequestBody ObjectNode articleBody, @PathVariable Long id,
			HttpServletRequest request, UriComponentsBuilder ucBuilder) throws JsonProcessingException {

		Article article = objectMapper.treeToValue(articleBody.get("article"), Article.class);
		String filePath = getArticleFile(article);
		File jsonFile = new File(filePath);

		String token = request.getHeader(tokenHeader);

		try {
			Article newArticle = dao.find(id);
			User user = userService.getUserFromToken(token);

			if (newArticle == null || !newArticle.getAutor().getUsername().equals(user.getUsername())) {
				return new ResponseEntity<String>("article not found", HttpStatus.NOT_FOUND);
			}
			// write the json into a file
			objectMapper.writeValue(jsonFile, articleBody.get("body"));

			JsonNode articleTagsNode = articleBody.get("article").get("tags");

			List<ArticleTag> articleTags = objectMapper.readValue(articleTagsNode.traverse(),
					new TypeReference<ArrayList<ArticleTag>>() {
					});

			newArticle.setTitle(article.getTitle());
			newArticle.setEnabled(false); 
			newArticle.setAutor(user);
			//newArticle.setWriteDate(LocalDateTime.now());
			newArticle.setLastEdited(LocalDateTime.now());
			newArticle.setLink(filePath);

			newArticle.setTags(new HashSet<ArticleTag>(articleService.handleTags(articleTags)));

			if (checker.checkIfValid(newArticle) && !checker.checkNotOwner(user.getUsername(), newArticle)) {
				dao.update(newArticle);

				HttpHeaders headers = new HttpHeaders();
				headers.setLocation(ucBuilder.path("/articles/{id}").buildAndExpand(newArticle.getId()).toUri());
				return new ResponseEntity<Article>(newArticle, headers, HttpStatus.CREATED);
			}

			return new ResponseEntity<String>("Title already exists", HttpStatus.CONFLICT);
		} catch (IOException e) {
			if (jsonFile.exists()) {
				if (!jsonFile.delete()) {
					return new ResponseEntity<String>(
							"there was an error saving the article," + "the file needs to be removed manually",
							HttpStatus.INTERNAL_SERVER_ERROR);
				}
			}
			return new ResponseEntity<String>("Error writing file", HttpStatus.INTERNAL_SERVER_ERROR);
		}

	}

	@PreAuthorize("hasRole('ADMIN')")
	@RequestMapping(value = "article/{id}", method = RequestMethod.DELETE)
	public ResponseEntity<Article> deleteArticle(@PathVariable Long id) {

		Article article = dao.find(id);
		if (article != null) {
			dao.removeArticle(article);
			return new ResponseEntity<Article>(article, HttpStatus.NO_CONTENT);
		}
		return new ResponseEntity<Article>(HttpStatus.NOT_FOUND);
	}
	
	private String getArticleFile(Article article){
		return this.articlesPaths + article.getId() + "_" + article.getTitle().replaceAll(" ", "_")+ ".json";
	}

}

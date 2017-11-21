package com.pier.controllers.user;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

import com.pier.config.Crawlers;
import com.pier.rest.model.Article;
import com.pier.rest.model.Brand;
import com.pier.rest.model.Category;
import com.pier.rest.model.ObjectModel;
import com.pier.rest.model.Product;
import com.pier.rest.model.ProductType;
import com.pier.service.ArticleDao;
import com.pier.service.BrandDao;
import com.pier.service.CategoryDao;
import com.pier.service.ProductDao;
import com.pier.service.ProductTypeDao;

@Controller
@EnableWebMvc
@RequestMapping(value="seo")
public class SearchEngineController {
	
	@Autowired
	ProductDao productDao;
	
	@Autowired
	ProductTypeDao productTypeDao;
	
	@Autowired
	CategoryDao categoryDao;
	
	@Autowired
	BrandDao brandDao;
	
	@Autowired
	ArticleDao articleDao;	
	
	@Value("${angular.articles}")
	String articlesPaths;
	
	@Value("${domain}")
	String domain;
	
	@Value("${angular.images}")
	String imagesPath;
	
	
	@RequestMapping(value="google/{entityType}/{id}",method=RequestMethod.GET)	
	public String getGoogleResult(@PathVariable String entityType, @PathVariable Long id,HttpServletRequest request,HttpServletResponse response){
		ObjectModel entity=null;
		switch(entityType){
		case Crawlers.ENTITY_ARTICLES:entity=articleDao.find(id); break;
		case Crawlers.ENTITY_BRANDS:entity=brandDao.find(id); break;
		case Crawlers.ENTITY_CATEGORIES:entity=categoryDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTS:entity=productDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTTYPES:entity=productTypeDao.find(id); break;
		default : entity=null;
		}
		String content=buildForGoogle(entity);
		
		response.setContentType(MediaType.TEXT_HTML_VALUE);
	    response.setCharacterEncoding("UTF-8");
	    
	    response.setStatus(content.length()>1?HttpServletResponse.SC_OK:HttpServletResponse.SC_NOT_FOUND);
		
		return content;
	}
	
	
	@RequestMapping(value="google/none",method=RequestMethod.GET)	
	public String getGoogleResultNone(HttpServletRequest request,HttpServletResponse response){		
		String content=buildForGoogle(null);
		
		response.setContentType(MediaType.TEXT_HTML_VALUE);
	    response.setCharacterEncoding("UTF-8");
	    
	    response.setStatus(content.length()>1?HttpServletResponse.SC_OK:HttpServletResponse.SC_NOT_FOUND);
		
		return content;
	}
	
	
	@RequestMapping(value="facebook/{entityType}/{id}",method=RequestMethod.GET)	
	public String getFacebookResult(@PathVariable String entityType, @PathVariable Long id,HttpServletRequest request,HttpServletResponse response){
		ObjectModel entity=null;
		switch(entityType){
		case Crawlers.ENTITY_ARTICLES:entity=articleDao.find(id); break;
		case Crawlers.ENTITY_BRANDS:entity=brandDao.find(id); break;
		case Crawlers.ENTITY_CATEGORIES:entity=categoryDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTS:entity=productDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTTYPES:entity=productTypeDao.find(id); break;
		default : entity=null;
		}
		
		String content=buildForFacebook(entity);
		
		response.setContentType(MediaType.TEXT_HTML_VALUE);
	    response.setCharacterEncoding("UTF-8");
	    
	    response.setStatus(content.length()>1?HttpServletResponse.SC_OK:HttpServletResponse.SC_NOT_FOUND);
		
		return content;
		
	}
	
	
	@RequestMapping(value="whatsapp/{entityType}/{id}",method=RequestMethod.GET)	
	public String getWhatsAppResult(@PathVariable String entityType, @PathVariable Long id,HttpServletRequest request,HttpServletResponse response){
		ObjectModel entity=null;
		switch(entityType){
		case Crawlers.ENTITY_ARTICLES:entity=articleDao.find(id); break;
		case Crawlers.ENTITY_BRANDS:entity=brandDao.find(id); break;
		case Crawlers.ENTITY_CATEGORIES:entity=categoryDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTS:entity=productDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTTYPES:entity=productTypeDao.find(id); break;
		default : entity=null;
		}
		
		String content=buildForWhatsApp(entity);
		
		response.setContentType(MediaType.TEXT_HTML_VALUE);
	    response.setCharacterEncoding("UTF-8");
	    
	    response.setStatus(content.length()>1?HttpServletResponse.SC_OK:HttpServletResponse.SC_NOT_FOUND);
		
		return content;
		
	}
	
	
	@RequestMapping(value="{entityType}/{id}",method=RequestMethod.GET)
	@ResponseBody
	public String getAllSEOsResult(@PathVariable String entityType, @PathVariable Long id,HttpServletRequest request, HttpServletResponse response){
		ObjectModel entity=null;
		switch(entityType){
		case Crawlers.ENTITY_ARTICLES:entity=articleDao.find(id); break;
		case Crawlers.ENTITY_BRANDS:entity=brandDao.find(id); break;
		case Crawlers.ENTITY_CATEGORIES:entity=categoryDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTS:entity=productDao.find(id); break;
		case Crawlers.ENTITY_PRODUCTTYPES:entity=productTypeDao.find(id); break;
		default : entity=null;
		}
		
		String content=buildForAll(entity);
		
		response.setContentType(MediaType.TEXT_HTML_VALUE);
	    response.setCharacterEncoding("UTF-8");
	    
	    response.setStatus(content.length()>1?HttpServletResponse.SC_OK:HttpServletResponse.SC_NOT_FOUND);
		
		return content;
		
	}
	
	private String buildForGoogle(ObjectModel entity) {
		if(entity==null)
			return nonIndexedGoogleMarkup();
		
		if(entity instanceof Article){
			Article article=(Article) entity;
			return parseMetaContentGoogle(article.getTitle(),article.getDescription()); 
		}else if(entity instanceof Product){
			Product product=(Product) entity;
			return parseMetaContentGoogle(product.getName(),product.getDescription());			
		}else if(entity instanceof Brand){
			Brand brand=(Brand) entity;
			return parseMetaContentGoogle(brand.getName(),brand.getName());	
		}else if(entity instanceof ProductType ){
			ProductType productType=(ProductType) entity;
			return parseMetaContentGoogle(productType.getName(),productType.getName());	
		}
		else if(entity instanceof Category){
			Category category=(Category) entity;
			return parseMetaContentGoogle(category.getName(),category.getName());	
		}else{
			return "";
		}
	}
	
	private String buildForFacebook(ObjectModel entity) {
		if(entity instanceof Article){
			Article article=(Article) entity;
			return parseMetaContentFacebook(article.getTitle(),article.getDescription(),"http://"+domain+"/articles/"+article.getId(),
					"http://"+domain+this.articlesPaths+ "images/art"+article.getId()+".jpg","article"); 
		}else if(entity instanceof Product){
			Product product=(Product) entity;
			return parseMetaContentFacebook(product.getName(),product.getDescription(),"http://"+domain+"/products/"+product.getId(),
					"http://"+domain+imagesPath+"+products/+prod_"+product.getId()+".jpg","product");			
		}else if(entity instanceof Brand){
			Brand brand=(Brand) entity;
			return parseMetaContentFacebook(brand.getName(),brand.getName(),"http://"+domain+"/products?brands="+brand.getId(),
					"http://"+domain+imagesPath+"+brands/"+brand.getId()+".jpg","website");	
		}else if(entity instanceof ProductType ){
			ProductType productType=(ProductType) entity;
			return parseMetaContentFacebook(productType.getName(),productType.getName(),"http://"+domain+"/products?types="+productType.getId(),
					"http://"+domain+imagesPath+"main_logo.jpg","website");	
		}
		else if(entity instanceof Category){
			Category category=(Category) entity;
			return parseMetaContentFacebook(category.getName(),category.getName(),"http://"+domain+"/products?categories="+category.getId(),
					"http://"+domain+imagesPath+"main_logo.jpg","website");	
		}else{
			return "";
		}
	}
	
	private String buildForWhatsApp(ObjectModel entity) {
		return buildForFacebook(entity);
	}
	
	private String buildForAll(ObjectModel entity) {
		//the og_url attr has to match the front end layer cannonical path to access the resource eg. http:www.yourmom.com/products/1
		if(entity instanceof Article){
			Article article=(Article) entity;
			return parseMetaContentAll(article.getTitle(),article.getDescription(),"http://"+domain+"/articles/"+article.getId(),
					"http://"+domain+"/"+this.articlesPaths+ "images/art"+article.getId(),"article"); 
		}else if(entity instanceof Product){
			Product product=(Product) entity;
			return parseMetaContentAll(product.getName(),product.getDescription(),"http://"+domain+"/products/"+product.getId(),
					"http://"+domain+"/"+imagesPath+"+products/+prod_"+product.getId()+".jpg","product");			
		}else if(entity instanceof Brand){
			Brand brand=(Brand) entity;
			return parseMetaContentAll(brand.getName(),brand.getName(),"http://"+domain+"/products?brands="+brand.getId(),
					"http://"+domain+"/"+imagesPath+"+brands/"+brand.getId()+".jpg","website");	
		}else if(entity instanceof ProductType ){
			ProductType productType=(ProductType) entity;
			return parseMetaContentAll(productType.getName(),productType.getName(),"http://"+domain+"/products?types="+productType.getId(),
					"http://"+domain+"/"+imagesPath+"main_logo.jpg","website");	
		}
		else if(entity instanceof Category){
			Category category=(Category) entity;
			return parseMetaContentAll(category.getName(),category.getName(),"http://"+domain+"/products?categories="+category.getId(),
					"http://"+domain+"/"+imagesPath+"main_logo.jpg","website");	
		}else{
			return "";
		}
	}
	
	private String parseMetaContentFacebook(String title, String description, String og_url, String og_image, String og_type){
		String htmlMarkup="<!DOCTYPE html>"+
		"<html>"+
		"<head>"+	
		//facebook & WhatsApp
		"<meta property=\"og:site_name\" content=\"mxphysique.com\">"+
		"<meta property=\"og:url\" content=\""+og_url+"\" />"+
		"<meta property=\"og:type\" content=\""+og_type+"\"/>"+
		"<meta property=\"og:title\" content=\""+title+"\" />"+
		"<meta property=\"og:description\" content=\""+description+"\" />"+
		"<meta property=\"og:image\" content=\""+og_image+"\" />"+
		"</head>"+
		"</html>";
		
		return htmlMarkup;
	}
	
	private String parseMetaContentGoogle(String title, String description){
		String htmlMarkup="<!DOCTYPE html>"+
		"<html>"+
		"<head>"+
		"<meta charset=\"utf-8\">"+
		"<meta name=\"Description\" CONTENT=\""+description+"\">"+		
		"<title>"+title+"</title>"+
		"<meta name=\"robots\" content=\"nofollow\">"+
		"</head>"+
		"</html>";
		
		return htmlMarkup;
	}
	
	private String nonIndexedGoogleMarkup(){
		String htmlMarkup="<!DOCTYPE html>"+
				"<html>"+
				"<head>"+
				"<meta name=\"googlebot\" content=\"noindex, nofollow\" />"+
				"<meta name=\"robots\" content=\"noindex, nofollow\">"+
				"</head>"+
				"</html>";
				
				return htmlMarkup;
	}
	
	
	private String parseMetaContentAll(String title, String description, String og_url, String og_image, String og_type){
		String htmlMarkup="<!DOCTYPE html>"+
		"<html>"+
		"<head>"+
		"<meta charset=\"utf-8\">"+
		"<meta name=\"Description\" CONTENT=\""+description+"\">"+		
		"<title>"+title+"</title>"+
		"<meta name=\"robots\" content=\"nofollow\">"+
		
		//facebook & WhatsApp
		"<meta property=\"og:site_name\" content=\"mxphysique.com\">"+
		"<meta property=\"og:url\" content=\""+og_url+"\" />"+
		"<meta property=\"og:type\" content=\""+og_type+"\"/>"+
		"<meta property=\"og:title\" content=\""+title+"\" />"+
		"<meta property=\"og:description\" content=\""+description+"\" />"+
		"<meta property=\"og:image\" content=\""+og_image+"\" />"+
		"</head>"+
		"</html>";
		
		return htmlMarkup;
	}
	

}

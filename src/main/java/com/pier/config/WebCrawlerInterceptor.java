package com.pier.config;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import javax.annotation.PostConstruct;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.ServletRequestUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

@Component
public class WebCrawlerInterceptor extends HandlerInterceptorAdapter {
	
	@Value("${facebook_crawler}")
	String facebookCrawlers;
	
	@Value("${google_crawler}")
	String googleCrawlers;
	
	@Value("${whatsapp_crawler}")
	String whatsappCrawlers;
	
	@Value("#{servletContext.contextPath}")
    private String servletContextPath;
	
	
	List<String> whatsAppAgents;
	List<String> googleAgents;
	List<String> facebookAgents;
	
	@PostConstruct
	void initialize(){
		facebookAgents=Arrays.asList(facebookCrawlers.split("::::"));
		googleAgents=Arrays.asList(googleCrawlers.split("::::"));
		whatsAppAgents=Arrays.asList(whatsappCrawlers.split("::::"));
	}
	
	
	private static final String USER_AGENT_HEADER = "User-Agent";
	
	 @Override
	 public boolean preHandle(HttpServletRequest request, 
			HttpServletResponse response, Object object) throws Exception {
		 
		final String userAgent = request.getHeader(USER_AGENT_HEADER);		
		final String method=request.getMethod();		
		final String requestURI = request.getRequestURI();
		@SuppressWarnings("unchecked")
		final Map<String, String> pathVariables = (Map<String, String>) request
                    .getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
		
		final String entityId=pathVariables.get("id");
		List<String> allCrawlers=new ArrayList<String>();
		allCrawlers.addAll(googleAgents);
		allCrawlers.addAll(facebookAgents);
		allCrawlers.addAll(whatsAppAgents);		
		
		boolean requestFromSEO=isAnyKeyContained(userAgent,allCrawlers);
		
		if(method.equalsIgnoreCase(RequestMethod.GET.name()) && entityId!=null && requestFromSEO){			
			/*if(isAnyKeyContained(userAgent,googleAgents))
				response.sendRedirect("/seo/google/"+getRequestedResource(requestURI)+"/"+entityId);
			else if(isAnyKeyContained(userAgent,facebookAgents))
				response.sendRedirect("/seo/facebook/"+getRequestedResource(requestURI)+"/"+entityId);	
			else if(isAnyKeyContained(userAgent,whatsAppAgents))
				response.sendRedirect("/seo/whatsapp/"+getRequestedResource(requestURI)+"/"+entityId);	*/
			
			//response.sendRedirect(servletContextPath+"/seo/"+getRequestedResource(requestURI)+"/"+entityId);	
			request.getRequestDispatcher("/seo/"+getRequestedResource(requestURI)+"/"+entityId).forward(request, response);
			
			return false;
		}else if(isAnyKeyContained(userAgent,googleAgents)){
			request.getRequestDispatcher("/seo/google/none").forward(request, response);
			return false;
		}else if(requestFromSEO){
			request.getRequestDispatcher("/seo").forward(request, response);
			return false;
		}
		
		return true;
	 }
	 
	 private boolean isAnyKeyContained(String sample,List<String> keywords){
		 
		  for(String keyword : keywords){
		      if(sample.contains(keyword)){
		         return true;
		      }
		   }
		   return false; 		 
	 }
	 
	 private String getRequestedResource(String requestURI){
		 List<String> resources=Arrays.asList(Crawlers.ENTITY_ARTICLES,Crawlers.ENTITY_PRODUCTS,Crawlers.ENTITY_CATEGORIES,Crawlers.ENTITY_PRODUCTTYPES,Crawlers.ENTITY_PRODUCTTYPES);
		 try{
		 return resources.stream().filter(resource->requestURI.contains(resource)).findFirst().get();
		 }catch(NoSuchElementException noEl){
			 return "";
		 }
		
	 }
	 

	/* @Override
	 public void postHandle(HttpServletRequest request, HttpServletResponse response, 
			Object object, ModelAndView model)
			throws Exception {
		System.out.println("_________________________________________");
		System.out.println("In postHandle request processing "
				+ "completed by @RestController");
		System.out.println("_________________________________________");
	 }

	 @Override
	 public void afterCompletion(HttpServletRequest request, HttpServletResponse response, 
			Object object, Exception arg3)
			throws Exception {
		System.out.println("________________________________________");
		System.out.println("In afterCompletion Request Completed");
		System.out.println("________________________________________");
	 }
	 */
	 
	 
	 @Bean(name="facebook")
	 public List<WebCrawler> getFacebookCrawlers(){		 
		 return Arrays.asList(facebookCrawlers.split("|")).stream().map(str->new WebCrawler("Facebook",str)).collect(Collectors.toList());		 
	 }
	 
	 
	 @Bean(name="google")
	 public List<WebCrawler> getGoogleCrawlers(){
		 
		 return Arrays.asList(googleCrawlers.split("|")).stream().map(str->new WebCrawler("Google",str)).collect(Collectors.toList());	 
	 }
	 
	 
	 @Bean(name="whatsapp")
	 public List<WebCrawler> getWhatsAppCrawlers(){		 
		 return Arrays.asList(googleCrawlers.split("|")).stream().map(str->new WebCrawler("WhatsApp",str)).collect(Collectors.toList());		 
	 }
	 
	 

}

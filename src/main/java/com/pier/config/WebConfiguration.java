package com.pier.config;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.DeviceHandlerMethodArgumentResolver;
import org.springframework.mobile.device.DeviceResolverHandlerInterceptor;
import org.springframework.mobile.device.DeviceWebArgumentResolver;
import org.springframework.mobile.device.site.SitePreferenceWebArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages = { "com.pier.controllers.*", "com.pier.config, com.pier.model.security",
		"com.pier.rest,com.pier.security.*" })
public class WebConfiguration extends WebMvcConfigurerAdapter {
	
	@Autowired
	WebCrawlerInterceptor crawlerInterceptor;
	
	@Override
	public void addCorsMappings(CorsRegistry registry) {
		registry.addMapping("/**")
		.allowedMethods("POST", "GET",  "PUT", "OPTIONS", "DELETE")
		.maxAge(3600);
	}

	@Override
	public void addResourceHandlers(ResourceHandlerRegistry registry) {
		registry.addResourceHandler("/store/**").addResourceLocations("/fstore-app/");
	}

	

	@Override
	public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {

		// Adding Spring mobile argument resolvers
		argumentResolvers.add(deviceHandlerMethodArgumentResolver());		
		argumentResolvers.add(new ServletWebArgumentResolverAdapter(new DeviceWebArgumentResolver()));
		argumentResolvers.add(new ServletWebArgumentResolverAdapter(new SitePreferenceWebArgumentResolver()));

	}
	
	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(deviceResolverHandlerInterceptor());
		//won't use this interceptor right now as our custom jboss httphandler is taking care ;)
		//registry.addInterceptor(crawlerInterceptor).addPathPatterns("/meta/**").excludePathPatterns("/seo/**");

	}
	
	@Bean
	public DeviceHandlerMethodArgumentResolver deviceHandlerMethodArgumentResolver() {
		return new DeviceHandlerMethodArgumentResolver();
	}
	
	 @Bean
     public DeviceResolverHandlerInterceptor deviceResolverHandlerInterceptor() {
         return new DeviceResolverHandlerInterceptor();
     } 
	

	
	  @Autowired SpecObjectMapper domainMapper;
	  
	  @Override public void  configureMessageConverters(List<HttpMessageConverter<?>> converters) {
		  
	  MappingJackson2HttpMessageConverter converter = new
	  MappingJackson2HttpMessageConverter();
	  
	  StringHttpMessageConverter stringConverter=new StringHttpMessageConverter(); 
	  
	  converter.setObjectMapper(domainMapper); 
	  converters.add(stringConverter);
	  converters.add(converter);	  
	  super.configureMessageConverters(converters); }
	 
	 
	 @Bean(name="multipartResolver") 
	    public CommonsMultipartResolver getResolver() throws IOException{
	        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
	         
	        //Set the maximum allowed size (in bytes) for each individual file.
	        resolver.setMaxUploadSizePerFile(20971520);//20MB
	         
	        //You may also set other available properties.
	         
	        return resolver;
	    }
	 
}

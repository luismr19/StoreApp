package com.pier.config;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mobile.device.DeviceWebArgumentResolver;
import org.springframework.mobile.device.site.SitePreferenceWebArgumentResolver;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.ServletWebArgumentResolverAdapter;

@EnableWebMvc
@Configuration
@ComponentScan(basePackages={"com.pier.controllers.*", "com.pier.config, com.pier.model.security", "com.pier.rest,com.pier.security.*"})
public class WebConfiguration extends WebMvcConfigurerAdapter {

	@Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry
          .addResourceHandler("/pages/**")
          .addResourceLocations("/pages/"); 
    }
	
	@Override
    public void addArgumentResolvers(
        List<HandlerMethodArgumentResolver> argumentResolvers) {

        // Adding Spring mobile argument resolvers
        argumentResolvers.add(
            new ServletWebArgumentResolverAdapter(
                new DeviceWebArgumentResolver()));

        argumentResolvers.add(
            new ServletWebArgumentResolverAdapter(
                new SitePreferenceWebArgumentResolver()));

    }
	
	/*@Autowired
	SpecObjectMapper domainMapper;
	
	@Override
	public void configureMessageConverters(List<HttpMessageConverter<?>> converters)
    {
      MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
      converter.setObjectMapper(domainMapper);
      converters.add(converter);
      super.configureMessageConverters(converters);
    }*/
}

package com.pier.config;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration;

import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.context.ContextLoaderListener;
import org.springframework.web.context.support.AnnotationConfigWebApplicationContext;
import org.springframework.web.servlet.DispatcherServlet;

public class ApplicationBootstrap implements WebApplicationInitializer {

	@Override
	public void onStartup(ServletContext servletContext) throws ServletException {
		AnnotationConfigWebApplicationContext  mainContext=new AnnotationConfigWebApplicationContext ();
		mainContext.register(SpringConfiguration.class);
		servletContext.addListener(new ContextLoaderListener(mainContext));
		
		AnnotationConfigWebApplicationContext webContext= new AnnotationConfigWebApplicationContext();
        webContext.setParent(mainContext);
        webContext.register(WebConfiguration.class);
        
        
        
        ServletRegistration.Dynamic app=servletContext.addServlet("deServlet", new DispatcherServlet(webContext));
        app.setLoadOnStartup(1);
        app.addMapping("/*");
        
        
      
		
	}	
	

}

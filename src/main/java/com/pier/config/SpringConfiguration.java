package com.pier.config;

import java.util.Properties;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

@Configuration
@ImportResource("classpath:/persistence-beans.xml")
@PropertySources({
@PropertySource("classpath:jwt2.properties"),
@PropertySource("classpath:paths.properties"),
@PropertySource("classpath:payment.properties"),
@PropertySource("classpath:app.properties"),
@PropertySource("classpath:crawlers.properties")
})
@EnableAspectJAutoProxy(proxyTargetClass=true)
public class SpringConfiguration {
	
	@Value("${mail.sender}")
	String sender;
	@Value("${mail.auth}")
	private String mailAuth;
	@Value("${mail.smtp.host}")
	String host;
	@Value("${mail.smtp.port}")
	String port;
	@Value("${mail.transport.protocol}")
	String protocol;
	@Value("${mail.smtp.auth}")
	String auth;
	@Value("${mail.smtp.starttls.enable")
	String tlsEnable;
	@Value("${mail.debug}")
	String debug;
	@Value("${mail.smtp.ssl.trust}")
	String trust;
			
	
	
	@Bean
	public JavaMailSenderImpl getJavaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost(host);
        mailSender.setPort(Integer.parseInt(port));
        
         
        mailSender.setUsername(sender);
        mailSender.setPassword(mailAuth);
         
        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.smtp.host",host);
        props.put("mail.smtp.port", port);
        props.put("mail.transport.protocol", protocol);
        props.put("mail.smtp.auth", auth);
        props.put("mail.smtp.starttls.enable", tlsEnable);
        props.put("mail.debug", debug);
        props.put("mail.smtp.ssl.trust", trust);
        props.put("mail.smtp.from", sender);
        
         
        return mailSender;
    }

}

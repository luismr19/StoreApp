package com.pier.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

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

}

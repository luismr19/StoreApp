package com.pier.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;

@Configuration
@ImportResource("classpath:/persistence-beans.xml")
@PropertySources({
@PropertySource("classpath:jwt2.properties"),
@PropertySource("classpath:paths.properties")
})
public class SpringConfiguration {

}

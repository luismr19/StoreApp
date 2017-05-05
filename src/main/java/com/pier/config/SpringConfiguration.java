package com.pier.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ImportResource("classpath:/persistence-beans.xml")
@PropertySource("classpath:jwt2.properties")
public class SpringConfiguration {

}

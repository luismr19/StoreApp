package com.pier.config;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer;

@Component
public class SpecObjectMapper extends ObjectMapper{
	
	public SpecObjectMapper(){
		
		JavaTimeModule timeModule=new JavaTimeModule();
		LocalDateTimeDeserializer localDateTimeDeserializer = new LocalDateTimeDeserializer(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
		timeModule.addDeserializer(LocalDateTime.class, localDateTimeDeserializer);
		
		registerModule(timeModule);
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false)
		.findAndRegisterModules()
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)        
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .setSerializationInclusion(Include.NON_NULL);
		
		
		
	}

}

package com.pier.config;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@Component
public class SpecObjectMapper extends ObjectMapper{
	
	public SpecObjectMapper(){
		
		registerModule(new JavaTimeModule() );
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,true)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .setSerializationInclusion(Include.NON_NULL);
		
		
		
	}

}

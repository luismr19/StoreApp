package com.pier.config;

import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.fasterxml.jackson.annotation.*;

@Component
public class SpecObjectMapper extends ObjectMapper{
	
	public SpecObjectMapper(){
		registerModule(new JavaTimeModule() );
		configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS,false)
        .configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false)
        .disable(DeserializationFeature.ADJUST_DATES_TO_CONTEXT_TIME_ZONE)
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);      
		
		
	}

}

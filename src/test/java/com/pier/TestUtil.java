package com.pier;

import java.io.IOException;
import java.nio.charset.Charset;

import org.springframework.http.MediaType;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class TestUtil {
	
	public static final String sampleToken="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJhZG1pbiIsImF1ZGllbmNlIjoid2ViIiwiY3JlYXRlZCI6IjIwMTctMDUtMDMgMTM6MDAiLCJleHAiOjE0OTQ0MzkyMTZ9.zTCnARViIb8SDMDd-k-3-9uWFNpmqJXhyygXkyPpUsLSrZLm37VlhTUXzwE7E0ZZmpRQeMZuLqSebAQ8hqGtjQ";
	 
    public static final MediaType APPLICATION_JSON_UTF8 = new MediaType(MediaType.APPLICATION_JSON.getType(), MediaType.APPLICATION_JSON.getSubtype(), Charset.forName("utf8"));
 
    public static byte[] convertObjectToJsonBytes(Object object) throws IOException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);
        return mapper.writeValueAsBytes(object);
    }
 
    public static String createStringWithLength(int length) {
        StringBuilder builder = new StringBuilder();
 
        for (int index = 0; index < length; index++) {
            builder.append("a");
        }
 
        return builder.toString();
    }
}
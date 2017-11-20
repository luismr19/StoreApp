package com.pier.config;

public class WebCrawler {	
	String name;
	String userAgent;	
	
	public WebCrawler(String name, String userAgent) {
		super();
		this.name = name;
		this.userAgent = userAgent;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getUserAgent() {
		return userAgent;
	}
	public void setUserAgent(String userAgent) {
		this.userAgent = userAgent;
	}
	
	
}

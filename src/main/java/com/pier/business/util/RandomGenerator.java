package com.pier.business.util;

import org.apache.commons.lang.RandomStringUtils;

public class RandomGenerator {
	
	static String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789~`!@#$%^&*()-_=+[{]}\\|;:\'\",<.>/?";
	static String charactersBasic = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
	
	public static String generateText(int length,boolean basic) {		
		String text = RandomStringUtils.random( length, basic?charactersBasic:characters);		
		return text;
	}

}

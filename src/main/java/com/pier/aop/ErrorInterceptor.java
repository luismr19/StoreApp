package com.pier.aop;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.ExpiredJwtException;

@Aspect
@Component
public class ErrorInterceptor {
	
	@Pointcut("within(com.pier.controllers.user..*)")
	public void controller() {}
	
	@Pointcut("execution(public org.springframework.http.ResponseEntity *(..))")// the pointcut expression
	private void allMethods() {}// the pointcut signature
	
	@Around("controller() && allMethods()")
	public Object errorInterceptor(ProceedingJoinPoint joinPoint) throws Throwable{
		
		try{
			return joinPoint.proceed();			
		}catch (IndexOutOfBoundsException exp){
			return new ResponseEntity<String>("error processing request",HttpStatus.UNAUTHORIZED);
		}		
				
		
		
	}
	

}

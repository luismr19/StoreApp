package com.pier.aop;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;





@Aspect
@Component
public class ErrorInterceptor {
  
  private static final Logger LOGGER = Logger.getLogger(ErrorInterceptor.class.getName());
  
  @Pointcut("within(com.pier.controllers.user..*)")
  public void controller() {}
  
  @Pointcut("execution(public org.springframework.http.ResponseEntity *(..))")// the pointcut expression
  private void allControllerMethods() {}// the pointcut signature
  
  @Around("controller() && allControllerMethods()")
  public Object userErrorInterceptor(ProceedingJoinPoint joinPoint) throws Throwable{
    
    try{
      return joinPoint.proceed();     
    }catch (IndexOutOfBoundsException exp){
      return new ResponseEntity<String>("error processing request",HttpStatus.UNAUTHORIZED);
    }
        
    
    
  }
  
  @Around("allControllerMethods()")
  public Object errorInterceptor(ProceedingJoinPoint joinPoint) throws Throwable{
    
    try{
      return joinPoint.proceed();     
    }catch (Exception exp){
      LOGGER.log( Level.SEVERE, exp.toString(), exp );
      return new ResponseEntity<String>("error processing request",HttpStatus.INTERNAL_SERVER_ERROR);
    }      
    
    
  }
  
  

}

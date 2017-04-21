package com.pier.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.*;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.pier.security.filters.JwtAuthenticationEntryPoint;
import com.pier.security.filters.JwtAuthenticationTokenFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	@Autowired
	JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

	@Autowired
	UserDetailsService userDetailsService;

	@Autowired
	protected void configureAuthentication(AuthenticationManagerBuilder auth) throws Exception {
		auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
	}

	@Bean
	PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean(name = BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	JwtAuthenticationTokenFilter generateJwtAuthenticationFilter() {
		return new JwtAuthenticationTokenFilter();
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception {
	    //web.ignoring().antMatchers("/login/**");
		web.ignoring().antMatchers("/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {

		http.csrf().disable()

				.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)

				.and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)

				.and().authorizeRequests()
				
                .antMatchers(
                        HttpMethod.GET,
                        "/",
                        "/*.html",
                        "/favicon.ico",
                        "/**/*.html",
                        "/**/*.css",
                        "/**/*.js"
                ).permitAll()
                //.antMatchers("/login/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().authenticated();
				
		http.addFilterBefore(generateJwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);


		super.configure(http);

		http.headers().cacheControl().disable();
	}

}

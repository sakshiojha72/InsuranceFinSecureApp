package com.ds.app.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.NoOpPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.ds.app.service.MyUserDetailServiceImpl;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

	@Autowired
	private MyUserDetailServiceImpl userDetailsService;
	
	@Autowired
	JWTFilter jwtFilter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		 http.csrf(csrf -> csrf.disable());

		 http.cors(cors->cors.disable());
		 
	        http.authorizeHttpRequests(auth -> auth
	        		
	        		 .requestMatchers(
	        	                "/v3/api-docs/**",
	        	                "/swagger-ui/**",
	        	                "/swagger-ui/index.html"
	        	            ).permitAll()
	        		
	                .requestMatchers("/finsecure/public/**").permitAll()
	                .requestMatchers("/finsecure/admin/**").hasAuthority("ADMIN")
	                .requestMatchers("/finsecure/hr/**").hasAnyAuthority("HR","ADMIN","MANAGER","EMPLOYEE")
	                //if any other role then 403 forbidden immediately
	                .requestMatchers("/finsecure/finance/**").hasAuthority("FINANCE")
	                .requestMatchers("/finsecure/system/**").hasAuthority("SYSTEM")
	                .requestMatchers("/finsecure/employee/**").hasAuthority("EMPLOYEE")
	        );

	        http.sessionManagement(session ->
	                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
	        );

	        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

	        return http.build();
	}
	
	@Bean
	PasswordEncoder passwordEncoder()
	{
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public AuthenticationManager authenticationManager(
	        AuthenticationConfiguration config) throws Exception {
	    return config.getAuthenticationManager();
	}
}

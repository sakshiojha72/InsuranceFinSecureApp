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

import com.ds.app.service.impl.MyUserDetailService;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig{

	@Autowired
	private MyUserDetailService userDetailsService;
	
	@Autowired
	JWTFilter jwtFilter;
	
	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception{
		 http.csrf(csrf -> csrf.disable());

		 http.cors(cors->{});
		 
		 http.authorizeHttpRequests(auth -> auth
				    .requestMatchers(
				        "/swagger-ui/**",
				        "/swagger-ui.html",
				        "/v3/api-docs/**"
				    ).permitAll()

				    .requestMatchers("/finsecure/public/**").permitAll()

				    .requestMatchers("/finsecure/admin/**").hasAuthority("ADMIN")
				    .requestMatchers("/finsecure/hr/**").hasAuthority("HR")
				    .requestMatchers("/finsecure/finance/**").hasAuthority("FINANCE")
				    .requestMatchers("/finsecure/system/**").hasAuthority("SYSTEM")

				    // IMPORTANT: this must come BEFORE /finsecure/employee/**
				    .requestMatchers("/finsecure/employee/employees").hasAnyAuthority("ADMIN", "HR")

				    .requestMatchers("/finsecure/employee/**").hasAuthority("EMPLOYEE")

				    .requestMatchers("/finsecure/insurance/**")
				    .hasAnyAuthority("EMPLOYEE", "ADMIN", "FINANCE", "HR")

				    .anyRequest().authenticated()
				);
		 
//		 http.authorizeHttpRequests(auth -> auth
//				    .requestMatchers("/finsecure/public/**").permitAll()
//				    .requestMatchers("/finsecure/admin/**").hasRole("ADMIN")
//				    .requestMatchers("/finsecure/hr/**").hasRole("HR")
//				    .requestMatchers("/finsecure/finance/**").hasRole("FINANCE")
//				    .requestMatchers("/finsecure/system/**").hasRole("SYSTEM")
//				    .requestMatchers("/finsecure/employee/**").hasRole("EMPLOYEE")
//				    .anyRequest().authenticated()
//				);


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

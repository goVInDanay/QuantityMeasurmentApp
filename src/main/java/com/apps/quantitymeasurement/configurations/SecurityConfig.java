package com.apps.quantitymeasurement.configurations;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.apps.quantitymeasurement.filters.JwtFilter;
import com.apps.quantitymeasurement.util.OAuth2SuccessHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	private final JwtFilter jwtFilter;
	private final OAuth2SuccessHandler successHandler;

	public SecurityConfig(OAuth2SuccessHandler successHandler, JwtFilter jwtFilter) {
		this.successHandler = successHandler;
		this.jwtFilter = jwtFilter;
	}

	@Bean
	SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/", "/login", "/error").permitAll().anyRequest().authenticated())
				.oauth2Login(oauth -> oauth.successHandler(successHandler))
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED));
		return http.build();
	}

}

package com.gateway.apigateway.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

@Configuration
public class SecurityConfig {

	@Bean
	SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
		return http
				.csrf(csrf -> csrf.disable()).authorizeExchange(exchange -> exchange
						.pathMatchers("/api/auth/**", "api/history/internal").permitAll().anyExchange().permitAll())
				.build();
	}
}
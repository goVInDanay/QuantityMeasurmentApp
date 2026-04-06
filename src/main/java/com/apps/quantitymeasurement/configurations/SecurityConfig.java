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
		http.csrf(csrf -> csrf.disable()).cors(cors -> {
		}).addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(auth -> auth.requestMatchers("/api/auth/**", "/oauth2/**").permitAll()
						.requestMatchers("/api/quantity/**").permitAll().requestMatchers("/api/history/**")
						.authenticated().requestMatchers("/api/user/**").authenticated().anyRequest().permitAll())
				.oauth2Login(oauth -> oauth.successHandler(successHandler))
				.exceptionHandling(ex -> ex.authenticationEntryPoint((request, response, authException) -> {
					String uri = request.getRequestURI();
					if (uri.startsWith("/api/")) {
						response.setStatus(401);
						response.setContentType("application/json");
						response.getWriter().write("{\"error\":\"Unauthorized\"}");
					} else {
						response.sendRedirect("/oauth2/authorization/google");
					}
				})).sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
		return http.build();
	}
}

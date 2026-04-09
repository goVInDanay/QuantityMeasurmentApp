package com.gateway.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpCookie;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.apigateway.config.SecurityConfig;
import com.gateway.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

	private final SecurityConfig securityConfig;
	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil, SecurityConfig securityConfig) {
		this.jwtUtil = jwtUtil;
		this.securityConfig = securityConfig;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		System.out.println("Gateway filter hit: " + path);

		if (path.startsWith("/api/auth") || path.startsWith("/api/history/internal")) {
			return chain.filter(exchange);
		}

		HttpCookie cookie = exchange.getRequest().getCookies().getFirst("JwtToken");
		if (cookie == null || !jwtUtil.validateToken(cookie.getValue())) {
			exchange.getResponse().setStatusCode(org.springframework.http.HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		try {
			String email = jwtUtil.getEmailFromToken(cookie.getValue());
			Long userId = jwtUtil.getUserIdFromToken(cookie.getValue());

			System.out.println("Extracted Email: " + email);
			System.out.println("Extracted UserId: " + userId);

			if (email != null && userId != null) {
				ServerHttpRequest decoratedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
					@Override
					public HttpHeaders getHeaders() {
						HttpHeaders headers = new HttpHeaders();
						headers.putAll(super.getHeaders());
						headers.add("X-User-Id", String.valueOf(userId));
						headers.add("X-User-Email", email);
						return headers;
					}
				};

				return chain.filter(exchange.mutate().request(decoratedRequest).build());
			}

		} catch (Exception e) {
			e.printStackTrace();
		}

		return chain.filter(exchange);
	}
}
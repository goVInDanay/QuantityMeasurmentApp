package com.gateway.apigateway.filter;

import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpRequestDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;

import com.gateway.apigateway.util.JwtUtil;

import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements GlobalFilter {

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
		String path = exchange.getRequest().getURI().getPath();
		System.out.println("Gateway filter hit: " + path);

		if (path.startsWith("/api/auth") || path.startsWith("/api/internal")) {
			return chain.filter(exchange);
		}

		HttpCookie cookie = exchange.getRequest().getCookies().getFirst("JwtToken");
		if (cookie == null || !jwtUtil.validateToken(cookie.getValue())) {
			exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
			return exchange.getResponse().setComplete();
		}

		String email = jwtUtil.getEmailFromToken(cookie.getValue());
		Long userId = jwtUtil.getUserIdFromToken(cookie.getValue());

		ServerHttpRequest mutatedRequest = new ServerHttpRequestDecorator(exchange.getRequest()) {
			@Override
			public HttpHeaders getHeaders() {
				HttpHeaders headers = new HttpHeaders();
				headers.putAll(super.getHeaders());
				headers.add("X-User-Email", email);
				headers.add("X-User-Id", String.valueOf(userId));
				return headers;
			}
		};
		return chain.filter(exchange.mutate().request(mutatedRequest).build());
	}
}
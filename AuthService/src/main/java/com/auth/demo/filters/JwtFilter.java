package com.auth.demo.filters;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.auth.demo.util.JwtUtil;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String token = null;
		String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			token = authHeader.substring(7);
		}

		if (token == null && request.getCookies() != null) {
			for (Cookie cookie : request.getCookies()) {
				if ("JwtToken".equals(cookie.getName())) {
					token = cookie.getValue();
				}
			}
		}

		if (token != null) {
			try {
				String email = jwtUtil.extractEmail(token);
				if (email != null && jwtUtil.isTokenValid(token, email)) {
					UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(email, null,
							List.of());

					SecurityContextHolder.getContext().setAuthentication(auth);
				}

			} catch (Exception e) {
				System.out.println("JWT Error: " + e.getMessage());
			}
		}

		filterChain.doFilter(request, response);
	}
}

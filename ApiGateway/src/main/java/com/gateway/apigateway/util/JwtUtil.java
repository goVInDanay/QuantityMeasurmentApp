package com.gateway.apigateway.util;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.util.function.Function;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;

@Component
public class JwtUtil {

	@Value("${jwt.secret}")
	private String secret;

	private Key signingKey;

	@PostConstruct
	public void init() {
		if (secret == null || secret.length() < 32) {
			throw new IllegalArgumentException("JWT secret must be at least 32 characters");
		}
		signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
	}

	public Claims extractAllClaims(String token) {
		return Jwts.parserBuilder().setSigningKey(signingKey).build().parseClaimsJws(token).getBody();
	}

	public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(token);
		return claimsResolver.apply(claims);
	}

	public boolean validateToken(String token) {
		try {
			extractAllClaims(token);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public Long getUserIdFromToken(String token) {
		Claims claims = extractAllClaims(token);
		Object userIdObj = claims.get("userId");
		if (userIdObj instanceof Integer) {
			return ((Integer) userIdObj).longValue();
		} else if (userIdObj instanceof Long) {
			return (Long) userIdObj;
		}
		return null;
	}

	public String getEmailFromToken(String token) {
		return extractAllClaims(token).getSubject();
	}
}
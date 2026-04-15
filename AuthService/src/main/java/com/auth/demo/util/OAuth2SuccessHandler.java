package com.auth.demo.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.auth.demo.entities.User;
import com.auth.demo.models.UserDto;
import com.auth.demo.service.AuthService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	@Value("${FRONTEND_URL}")
	private String frontendUrl;

	private final JwtUtil jwtUtil;
	private final AuthService userService;

	public OAuth2SuccessHandler(JwtUtil jwtUtil, AuthService userService) {
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		OAuth2User oauthUser = (OAuth2User) authentication.getPrincipal();
		String email = oauthUser.getAttribute("email");
		UserDto userDto = UserDto.builder().email(email).name(oauthUser.getAttribute("name"))
				.pictureUrl(oauthUser.getAttribute("picture")).build();
		User user = userService.saveOrUpdate(userDto, "GOOGLE");
		String token = jwtUtil.generateToken(email, user.getId());
		Cookie jwtCookie = new Cookie("JwtToken", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setSecure(true);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(24 * 60 * 60);
		if (!request.getServerName().equals("localhost")) {
			jwtCookie.setSecure(true);
			jwtCookie.setPath("/");
		}
		String cookieHeader = String.format("JwtToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None", token,
				24 * 60 * 60);

		response.addHeader("Set-Cookie", cookieHeader);
		response.sendRedirect(frontendUrl + "/dashboard");
	}
}

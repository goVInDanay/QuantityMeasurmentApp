package com.apps.quantitymeasurement.util;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.model.UserDto;
import com.apps.quantitymeasurement.service.UserService;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {

	@Value("${FRONTEND_URL}")
	private String frontendUrl;
	
	private final JwtUtil jwtUtil;
	private final UserService userService;

	public OAuth2SuccessHandler(JwtUtil jwtUtil, UserService userService) {
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

		String token = jwtUtil.generateToken(email);
		Cookie jwtCookie = new Cookie("JwtToken", token);
		jwtCookie.setHttpOnly(true);
		jwtCookie.setSecure(false);
		jwtCookie.setPath("/");
		jwtCookie.setMaxAge(24 * 60 * 60);

		response.addCookie(jwtCookie);
		response.sendRedirect(frontendUrl + "/dashboard");
	}
}

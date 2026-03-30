package com.apps.quantitymeasurement.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.model.UserDto;
import com.apps.quantitymeasurement.service.UserService;

@RestController
@RequestMapping("/api/user")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/profile")
	public ResponseEntity<UserDto> getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email;

		if (principal instanceof OAuth2User oauthUser) {
			email = oauthUser.getAttribute("email");
		} else if (principal instanceof String s) {
			email = (String) s;
		} else {
			throw new IllegalStateException("Unknown principal type: " + principal.getClass());
		}

		User user = userService.getByEmail(email);
		return ResponseEntity.ok(
				UserDto.builder().email(user.getEmail()).name(user.getName()).pictureUrl(user.getPictureUrl()).build());
	}
}

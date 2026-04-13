package com.user.userservice.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.user.userservice.dto.UserDto;
import com.user.userservice.service.UserService;
import com.user.userservice.utils.RequestUtils;

@RestController
@RequestMapping("/api")
public class UserController {
	private final UserService userService;

	public UserController(UserService userService) {
		this.userService = userService;
	}

	@GetMapping("/users/profile")
	public ResponseEntity<?> getProfile() {
		String email = RequestUtils.getEmail();

		if (email == null) {
			return ResponseEntity.status(401).build();
		}

		return ResponseEntity.ok("Hello " + email);
	}

	@PostMapping("/internal/users")
	public ResponseEntity<?> createOrUpdateUser(@RequestBody UserDto userDto) {
		userService.saveOrUpdate(userDto, "LOCAL");
		return ResponseEntity.ok().build();
	}
}

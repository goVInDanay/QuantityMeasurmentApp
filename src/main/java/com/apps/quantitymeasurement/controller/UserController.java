package com.apps.quantitymeasurement.controller;

import org.springframework.http.ResponseEntity;
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
		User user = userService.getCurrentUser();
		return ResponseEntity.ok(
				UserDto.builder().email(user.getEmail()).name(user.getName()).pictureUrl(user.getPictureUrl()).build());
	}
}

package com.auth.demo.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.auth.demo.client.UserClient;
import com.auth.demo.entities.User;
import com.auth.demo.exceptions.DatabaseException;
import com.auth.demo.models.LoginRequestDto;
import com.auth.demo.models.RegisterRequestDto;
import com.auth.demo.models.UserDto;
import com.auth.demo.service.AuthService;
import com.auth.demo.util.JwtUtil;
import com.auth.demo.util.PasswordValidator;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final JwtUtil jwtUtil;
	private final AuthService userService;
	private final UserClient userClient;

	public AuthController(JwtUtil jwtUtil, AuthService userService, UserClient userClient) {
		this.jwtUtil = jwtUtil;
		this.userService = userService;
		this.userClient = userClient;
	}

	@PostMapping("/register")
	public ResponseEntity<?> register(@Valid @RequestBody RegisterRequestDto request, HttpServletResponse response) {
		if (userService.existsByEmail(request.getEmail())) {
			throw new DatabaseException("Email already exists");
		}
		if (!PasswordValidator.isValid(request.getPassword())) {
			throw new DatabaseException(
					"Password must contain uppercase, lowercase, number, special character and be at least 8 characters long");
		}
		User user = userService.register(request);
		String token = jwtUtil.generateToken(user.getEmail(), user.getId());
		String cookieHeader = String.format("JwtToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None", token,
				24 * 60 * 60);

		response.addHeader("Set-Cookie", cookieHeader);
		try {
			UserDto dto = new UserDto();
			dto.setEmail(user.getEmail());
			dto.setName(user.getName());

			userClient.createUser(dto);
		} catch (Exception e) {
			System.out.println("User service down: " + e.getMessage());
		}
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {

		User user = userService.authenticate(request.getEmail(), request.getPassword());
		String token = jwtUtil.generateToken(user.getEmail(), user.getId());
		String cookieHeader = String.format("JwtToken=%s; Max-Age=%d; Path=/; HttpOnly; Secure; SameSite=None", token,
				24 * 60 * 60);

		response.addHeader("Set-Cookie", cookieHeader);
		return ResponseEntity.ok("Login Successful");
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		String cookieHeader = String.format("JwtToken=; Max-Age=0; Path=/; HttpOnly; Secure; SameSite=None");

		response.addHeader("Set-Cookie", cookieHeader);

		return ResponseEntity.ok("Logged out successfully");
	}
}

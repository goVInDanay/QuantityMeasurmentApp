package com.apps.quantitymeasurement.controller;

import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.exception.DatabaseException;
import com.apps.quantitymeasurement.model.LoginRequestDto;
import com.apps.quantitymeasurement.model.RegisterRequestDto;
import com.apps.quantitymeasurement.service.UserService;
import com.apps.quantitymeasurement.util.JwtUtil;
import com.apps.quantitymeasurement.util.PasswordValidator;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
	private final JwtUtil jwtUtil;
	private final UserService userService;

	public AuthController(JwtUtil jwtUtil, UserService userService) {
		this.jwtUtil = jwtUtil;
		this.userService = userService;
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
		String token = jwtUtil.generateToken(user.getEmail());
		ResponseCookie cookie = ResponseCookie.from("JwtToken", token).httpOnly(true).secure(false).path("/")
				.maxAge(24 * 60 * 60).sameSite("Lax").build();
		response.addHeader("Set-Cookie", cookie.toString());
		return ResponseEntity.ok("User registered successfully");
	}

	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequestDto request, HttpServletResponse response) {

		User user = userService.authenticate(request.getEmail(), request.getPassword());
		String token = jwtUtil.generateToken(user.getEmail());
		Cookie cookie = new Cookie("JwtToken", token);
		cookie.setHttpOnly(true);
		cookie.setPath("/");
		cookie.setMaxAge(24 * 60 * 60);
		response.addCookie(cookie);
		return ResponseEntity.ok("Login Successful");
	}

	@PostMapping("/logout")
	public ResponseEntity<?> logout(HttpServletResponse response) {
		ResponseCookie cookie = ResponseCookie.from("token", "").httpOnly(true).secure(false).path("/").maxAge(0)
				.build();
		response.addHeader("Set-Cookie", cookie.toString());
		return ResponseEntity.ok("Logged out successfully");
	}
}

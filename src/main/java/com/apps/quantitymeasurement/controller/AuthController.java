package com.apps.quantitymeasurement.controller;

import com.apps.quantitymeasurement.service.UserService;
import com.apps.quantitymeasurement.util.JwtUtil;

public class AuthController {
	private final JwtUtil jwtUtil;
	private final UserService userService;

	public AuthController(JwtUtil jwtUtil, UserService userService) {
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

}

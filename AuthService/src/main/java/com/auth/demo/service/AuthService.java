package com.auth.demo.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.auth.demo.client.UserClient;
import com.auth.demo.entities.User;
import com.auth.demo.models.RegisterRequestDto;
import com.auth.demo.models.UserDto;
import com.auth.demo.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class AuthService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;
	private final UserClient userClient;

	public AuthService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, UserClient userClient) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
		this.userClient = userClient;
	}

	public boolean existsByEmail(String email) {
		return userRepository.findByEmail(email).isPresent();
	}

	public User register(RegisterRequestDto request) {
		User user = User.builder().email(request.getEmail()).name(request.getName())
				.password(passwordEncoder.encode(request.getPassword())).provider("LOCAL").build();
		return userRepository.save(user);
	}

	@Transactional
	public User saveOrUpdate(UserDto userDto, String provider) {
		return userRepository.findByEmail(userDto.getEmail()).map(user -> updateExistingUser(user, userDto))
				.orElseGet(() -> createNewUser(userDto, provider));
	}

	private User updateExistingUser(User user, UserDto userDto) {
		user.setName(userDto.getName());
		user.setPictureUrl(userDto.getPictureUrl());
		return userRepository.save(user);
	}

	private User createNewUser(UserDto userDto, String provider) {
		User user = User.builder().email(userDto.getEmail()).name(userDto.getName()).pictureUrl(userDto.getPictureUrl())
				.provider(provider).build();
		return userRepository.save(user);
	}

	public User getByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
	}

	public User authenticate(String email, String password) {
		System.out.println(email + " " + password);
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("Invalid credentials");
		}
		UserDto dto = new UserDto();
		dto.setEmail(user.getEmail());
		dto.setName(user.getName());

		userClient.createUser(dto);
		return user;
	}

	public User getCurrentUser() {
		var authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return null;
		}
		Object principal = authentication.getPrincipal();

		if (principal instanceof OAuth2User oauthUser) {
			String email = oauthUser.getAttribute("email");
			return userRepository.findByEmail(email).orElse(null);
		}
		if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			String email = userDetails.getUsername();
			return userRepository.findByEmail(email).orElse(null);
//			throw new IllegalStateException("U	nknown principal type: " + principal.getClass());
		}
		if (principal instanceof String email) {
			return userRepository.findByEmail(email).orElse(null);
		}
		return null;
	}
}

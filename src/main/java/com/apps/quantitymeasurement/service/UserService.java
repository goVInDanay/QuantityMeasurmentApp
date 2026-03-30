package com.apps.quantitymeasurement.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.model.RegisterRequestDto;
import com.apps.quantitymeasurement.model.UserDto;
import com.apps.quantitymeasurement.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;
	private final BCryptPasswordEncoder passwordEncoder;

	public UserService(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		this.userRepository = userRepository;
		this.passwordEncoder = passwordEncoder;
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
		User user = userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));
		if (!passwordEncoder.matches(password, user.getPassword())) {
			throw new IllegalArgumentException("Invalid credentials");
		}

		return user;
	}

	public User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
		String email;
		if (principal instanceof OAuth2User oauthUser) {
			email = oauthUser.getAttribute("email");
		} else if (principal instanceof org.springframework.security.core.userdetails.User userDetails) {
			email = userDetails.getUsername();
		} else if (principal instanceof String s) {
			email = s;
		} else {
			throw new IllegalStateException("Unknown principal type: " + principal.getClass());
		}
		return getByEmail(email);
	}
}

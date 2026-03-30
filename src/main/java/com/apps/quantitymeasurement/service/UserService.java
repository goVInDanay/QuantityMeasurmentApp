package com.apps.quantitymeasurement.service;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import com.apps.quantitymeasurement.entities.User;
import com.apps.quantitymeasurement.model.UserDto;
import com.apps.quantitymeasurement.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
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

	public User getCurrentUser() {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		String email;
		if (principal instanceof OAuth2User oauthUser) {
			email = oauthUser.getAttribute("email");
		} else if (principal instanceof String s) {
			email = (String) s;
		} else {
			throw new IllegalStateException("Unknown principal type: " + principal.getClass());
		}

		return getByEmail(email);
	}
}

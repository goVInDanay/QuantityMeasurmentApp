package com.user.userservice.service;

import org.springframework.stereotype.Service;

import com.user.userservice.dto.UserDto;
import com.user.userservice.entities.User;
import com.user.userservice.repository.UserRepository;

import jakarta.transaction.Transactional;

@Service
public class UserService {
	private final UserRepository userRepository;

	public UserService(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	public boolean existsByEmail(String email) {
		return userRepository.findByEmail(email).isPresent();
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

	public User findByEmail(String email) {
		return userRepository.findByEmail(email)
				.orElseThrow(() -> new IllegalArgumentException("User not found with email: " + email));
	}
}

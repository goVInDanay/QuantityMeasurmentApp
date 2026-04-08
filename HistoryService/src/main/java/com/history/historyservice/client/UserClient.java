package com.history.historyservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

import com.history.historyservice.model.UserDto;

@FeignClient(name = "user-service")
public interface UserClient {

	@GetMapping("/api/users/profile")
	UserDto getUserByEmail(@RequestHeader("X-User-Email") String email);
}
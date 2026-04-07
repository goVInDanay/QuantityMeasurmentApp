package com.auth.demo.models;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequestDto {
	@NotBlank(message = "Name is required")
	private String name;
	@Email(message = "Invalid Email")
	private String email;
	@NotBlank(message = "Password is required")
	private String password;
}

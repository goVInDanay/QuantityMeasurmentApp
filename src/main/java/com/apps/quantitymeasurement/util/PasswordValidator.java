package com.apps.quantitymeasurement.util;

public class PasswordValidator {

	private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@#$%^&*()\\-+=]).{8,}$";
	public static boolean isValid(String password) {
		return password != null && password.matches(PASSWORD_REGEX);
	}
}

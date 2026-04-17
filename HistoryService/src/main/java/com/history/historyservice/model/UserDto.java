package com.history.historyservice.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
	private Long id;
	private String email;
	private String name;
	private String pictureUrl;
}
package com.cognizant.accountservice.model;

import lombok.*;



@ToString
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
	
	@Getter
	@Setter
	private String userid;
	@Getter
	@Setter
	private String name;
	@Getter
	@Setter
	private boolean isValid;
}
package com.cognizant.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

// for inputing error response

@AllArgsConstructor
@NoArgsConstructor
public class AccountErrorResponse {

	@Getter
	@Setter
	private LocalDateTime timestamp;
	@Getter
	@Setter
	private HttpStatus status;
	@Getter
	@Setter
	private String reason;
	@Getter
	@Setter
	private String message;
}

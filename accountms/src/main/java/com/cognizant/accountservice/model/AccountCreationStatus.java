package com.cognizant.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Id;


@NoArgsConstructor
@AllArgsConstructor
public class AccountCreationStatus {

	
	 //AccountCreationStatus for returning response
	@Id
	@Getter
	@Setter
	private long accountId;
	@Getter
	@Setter
	private String reason;


}

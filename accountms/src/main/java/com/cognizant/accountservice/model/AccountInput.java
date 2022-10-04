package com.cognizant.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
//taking input while transaction takes place


@NoArgsConstructor
@AllArgsConstructor
public class AccountInput {
	
	
	@Getter
	@Setter
	@NotNull(message = "Account number is mandatory")
	private long accountId;
	@Getter
	@Setter
	@NotNull(message = "Amount is mandatory")
	private double amount;

}
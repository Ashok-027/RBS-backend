package com.cognizant.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class TransactionInput {
	
	private AccountInput sourceAccount;
	private AccountInput targetAccount;
	@Positive(message = "Transfer amount must be positive")
	@Min(value = 1, message = "Amount must be larger than 1")
	private double amount;
	private String reference;

}
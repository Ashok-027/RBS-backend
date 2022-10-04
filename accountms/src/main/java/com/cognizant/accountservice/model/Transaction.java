package com.cognizant.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;



@AllArgsConstructor
@NoArgsConstructor
public class Transaction {
	

	@Getter
	@Setter
	private long id;
	@Getter
	@Setter
	private long sourceAccountId;
	@Getter
	@Setter
	private String sourceOwnerName;
	@Getter
	@Setter
	private long targetAccountId;
	@Getter
	@Setter
	private String targetOwnerName;
	@Getter
	@Setter
	private double amount;
	@Getter
	@Setter
	private LocalDateTime initiationDate;
	@Getter
	@Setter
	private String reference;

}
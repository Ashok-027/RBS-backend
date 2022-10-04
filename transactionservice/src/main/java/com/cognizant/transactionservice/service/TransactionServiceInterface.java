package com.cognizant.transactionservice.service;

import com.cognizant.transactionservice.models.Transaction;
import com.cognizant.transactionservice.util.AccountInput;
import com.cognizant.transactionservice.util.TransactionInput;

import java.text.ParseException;
import java.util.List;


public interface TransactionServiceInterface {

	/**
	 * @param token
	 * @param transactionInput
	 * @return boolean
	 */
	public boolean makeTransfer(String token, TransactionInput transactionInput);

	/**
	 * @param token
	 * @param accountInput
	 * @return boolean
	 */
	public boolean makeWithdraw(String token, AccountInput accountInput);

	/**
	 * @param token
	 * @param accountInput
	 * @return boolean
	 */
	public boolean makeDeposit(String token, AccountInput accountInput);
	
	/**
	 * @param token
	 * @param accountInput
	 * @return boolean
	 */
	public boolean makeServiceCharges(String token, AccountInput accountInput);

	public List<Transaction> getAccountStatement(long accountId, String from, String to) throws ParseException;

	public List<Transaction> getAccountStatement(long accountId);
}

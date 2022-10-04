package com.cognizant.accountservice.service;
import java.text.ParseException;
import java.util.List;
import java.util.Map;

import com.cognizant.accountservice.exceptionhandling.WrongDateFormatException;
import com.cognizant.accountservice.exceptionhandling.WrongDateProvidedException;
import com.cognizant.accountservice.model.*;


public interface AccountService {

	/**
	 * @param customerId
	 * @param account
	 * @return AccountCreationStatus
	 */
	public AccountCreationStatus createAccount(String token,String customerId, Account account);

	/**
	 * @param token
	 * @param customerId
	 * @return List<Account>
	 */
	public List<Account> getCustomerAccount(String token, String customerId);

	/**
	 * @param accountId
	 * @return Account
	 */
	public Account getAccount(long accountId);

	/**
	 * @param token
	 * @return AuthenticationResponse
	 */
	public AuthenticationResponse hasPermission(String token);

	/**
	 * @param token
	 * @return AuthenticationResponse
	 */
	public AuthenticationResponse hasEmployeePermission(String token);

	/**
	 * @param token
	 * @return AuthenticationResponse
	 */
	public AuthenticationResponse hasCustomerPermission(String token);

	/**
	 * @param accountInput
	 * @return Account
	 */
	public Account updateDepositBalance(AccountInput accountInput);
	public AuthenticationResponse hasAccountAccess(String token,long accountId);
	/**
	 * @param accountInput
	 * @return Account
	 */
	public Account updateBalance(AccountInput accountInput);

	/**
	 * @return List<Account>
	 */
	public List<Account> getAllAccounts();


	Map<String,Object> getAccountStatement(long accountId, String from, String to) throws ParseException;

	Map<String,Object> getAccountStatement(long accountId);

	void deleteCustomer(String id);
}

package com.cognizant.accountservice.service;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cognizant.accountservice.feignclient.CustomerFeignProxy;
import com.cognizant.accountservice.model.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.accountservice.exceptionhandling.AccessDeniedException;
import com.cognizant.accountservice.exceptionhandling.AccountNotFoundException;
import com.cognizant.accountservice.feignclient.AuthFeignClient;
import com.cognizant.accountservice.feignclient.TransactionFeign;
import com.cognizant.accountservice.repository.AccountRepository;


@Service
public class AccountServiceImpl implements AccountService {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountServiceImpl.class);
	@Autowired
	private AuthFeignClient authFeignClient;
	@Autowired
	private AccountRepository accountRepository;
	@Autowired
	private TransactionFeign transactionFeign;

	@Autowired
	private CustomerFeignProxy customerFeignProxy;


	//create customer aaccount
	@Override
	public AccountCreationStatus createAccount(String token,String customerId, Account account) {
		Date date = new Date();
		account.setOpeningDate(date);
		account.setOwnerName(customerFeignProxy.getCustomerDetails(token,customerId).getUsername());
		accountRepository.save(account);
		AccountCreationStatus accountCreationStatus = new AccountCreationStatus(account.getAccountId(),"A/C no. "+account.getAccountId()+" is created Successfully");
		LOGGER.info("Account Created Successfully");
		return accountCreationStatus;
	}


	//get customer account
	@Override
	public List<Account> getCustomerAccount(String token, String customerId) {
		List<Account> accountList = accountRepository.findByCustomerId(customerId);
		for (Account acc : accountList) {
			acc.setTransactions(transactionFeign.getTransactionsByAccId(token, acc.getAccountId()));
		}
		return accountList;
	}


	// get account by id
	@Override
	public Account getAccount(long accountId) {
		Account account = accountRepository.findByAccountId(accountId);
		if (account == null) {
			throw new AccountNotFoundException("Account Does Not Exist");
		}
		return account;
	}
	// withdrawn balance
	@Override
	public Account updateBalance(AccountInput accountInput) {
		LOGGER.info("Account to update" + accountInput.getAccountId());
		Account toUpdateAcc = accountRepository.findByAccountId(accountInput.getAccountId());
		toUpdateAcc.setCurrentBalance(toUpdateAcc.getCurrentBalance() - accountInput.getAmount());
		return accountRepository.save(toUpdateAcc);
	}


	//deposit balance
	@Override
	public Account updateDepositBalance(AccountInput accountInput) {
		LOGGER.info("Account update" + accountInput.getAccountId());
		Account toUpdateAcc = accountRepository.findByAccountId(accountInput.getAccountId());
		toUpdateAcc.setCurrentBalance(toUpdateAcc.getCurrentBalance() + accountInput.getAmount());
		return accountRepository.save(toUpdateAcc);
	}


	//validate token
	@Override
	public AuthenticationResponse hasPermission(String token) {
		return authFeignClient.tokenValidation(token);
	}

	//validity and Employee permissions check

	@Override
	public AuthenticationResponse hasEmployeePermission(String token) {
		AuthenticationResponse validity = authFeignClient.tokenValidation(token);
		if (!authFeignClient.getRole(validity.getUserid()).equalsIgnoreCase("EMPLOYEE")) {
			throw new AccessDeniedException("NOT ALLOWED");
		}
		return validity;
	}


	//validity and customer permissions check
	@Override
	public AuthenticationResponse hasCustomerPermission(String token) {
		AuthenticationResponse validity = authFeignClient.tokenValidation(token);
		if (!authFeignClient.getRole(validity.getUserid()).equalsIgnoreCase("CUSTOMER")) {
			throw new AccessDeniedException("NOT ALLOWED");
		}
		return validity;
	}
	@Override
	public AuthenticationResponse hasAccountAccess(String token,long accountId) {
		AuthenticationResponse validity = hasCustomerPermission(token);
		if (validity.getUserid().equalsIgnoreCase(accountRepository.findCustomerId(accountId))) {
			return validity;
		} else {
			throw new AccessDeniedException("NOT ALLOWED");
		}
	}

		@Override
	public List<Account> getAllAccounts() {
		List<Account> accounts = accountRepository.findAll();
		return accounts;
	}

	// Deleting the account details associated with the given account id
	@Override
	public void deleteCustomer(String id) {
		List<Account> list = new ArrayList<>();
		list = getAllAccounts();
		for (Account account : list) {
			if (account.getCustomerId().equalsIgnoreCase(id)) {
				accountRepository.deleteById(account.getAccountId());
			}
		}

	}


	@Override
	public Map<String,Object> getAccountStatement(long accountId) {
		Map<String,Object> map = (Map<String, Object>) transactionFeign.getAccountStatement(accountId);
		return map;
	}

	// Getting the account statements between the given dates
	@Override
	public Map<String,Object> getAccountStatement(long accountId, String from, String to) throws ParseException {
		Map<String,Object> map = (Map<String, Object>) transactionFeign.getAccountStatement(accountId, from, to);
		return map;
	}


	/**
	 * @param from
	 * @return boolean
	 */
	public boolean dateValidation(String from) {
		// TODO Auto-generated method stub
		//REGEX for dd/mm/yyyy
		String regex = "^(3[01]|[12][0-9]|0[1-9])/(1[0-2]|0[1-9])/[0-9]{4}$";
		Pattern pattern = Pattern.compile(regex);
		Matcher matcher = pattern.matcher((CharSequence)from);
		return matcher.matches();
	}

}
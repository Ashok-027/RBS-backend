package com.cognizant.accountservice.controller;

import com.cognizant.accountservice.exceptionhandling.MinimumBalanceException;
import com.cognizant.accountservice.exceptionhandling.WrongDateFormatException;
import com.cognizant.accountservice.exceptionhandling.WrongDateProvidedException;
import com.cognizant.accountservice.feignclient.TransactionFeign;
import com.cognizant.accountservice.model.*;
import com.cognizant.accountservice.repository.AccountRepository;
import com.cognizant.accountservice.service.AccountServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


@RestController
public class AccountController {

	private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

	@Autowired
	private MessageDetails messageDetails;
	@Autowired
	private AccountServiceImpl accountServiceImpl;
	@Autowired
	private TransactionFeign transactionFeign;

	@Autowired
	private AccountRepository accountRepository;


	/**
	 * @param token
	 * @param accountId
	 * @return ResponseEntity<Account>

	 */
	// Rest End point which Returns particular account details based on the ID
	@GetMapping("/getAccount/{accountId}")
	public ResponseEntity<Account> getAccount(@RequestHeader("Authorization") String token,@PathVariable long accountId) {

		//check if user has permission to get Account details
		accountServiceImpl.hasPermission(token);

		Account accountReturnObject = accountServiceImpl.getAccount(accountId);
		LOGGER.info("Account Details Returned Sucessfully");
		return new ResponseEntity<>(accountReturnObject, HttpStatus.OK);
	}


	/**
	 * @param token
	 * @param customerId
	 * @param account
	 * @return ResponseEntity<>

	 */
	//Rest End point to create account for a Customer by his/her ID
	@PostMapping("/createAccount/{customerId}")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<?> createAccount(@RequestHeader("Authorization") String token,@PathVariable String customerId,@Valid @RequestBody Account account) {
		accountServiceImpl.hasEmployeePermission(token);
		AccountCreationStatus returnObjAccountCreationStatus = accountServiceImpl.createAccount(token,customerId, account);
		if (returnObjAccountCreationStatus == null) {
			LOGGER.error("Customer Creation Unsucessful");
			return new ResponseEntity<>("Customer Creation Unsucessful", HttpStatus.NOT_ACCEPTABLE);
		}

		LOGGER.info("Account Created Sucessfully");
		return new ResponseEntity<>(returnObjAccountCreationStatus, HttpStatus.CREATED);
	}


	/**
	 * @param token
	 * @param customerId
	 * @return ResponseEntity<List<Account>>

	 */

	//Rest End point to get all the accounts details
	@GetMapping("/getAccounts/{customerId}")
	public ResponseEntity<List<Account>> getCustomerAccount(@RequestHeader("Authorization") String token,@PathVariable String customerId) {
		accountServiceImpl.hasPermission(token);
		LOGGER.info("Account List Returned");
		return new ResponseEntity<>(accountServiceImpl.getCustomerAccount(token, customerId), HttpStatus.OK);
	}

	//if no date is provided
	/**
	 * @param token
	 * @return ResponseEntity<List<Statement>>

	 */

	// Rest end point to get Account statement of all accounts
	@GetMapping("/getAccountStatement/{accountId}")
	public ResponseEntity<Map<String,Object>> getAccountStatement(@RequestHeader("Authorization") String token, @PathVariable long accountId){
		accountServiceImpl.hasPermission(token);
		Map<String,Object> statements = accountServiceImpl.getAccountStatement(accountId);
		Account account = accountRepository.findByAccountId(accountId);
		statements.put("balance",account.getCurrentBalance());
		LOGGER.info("Account Statement List Returned");
		return new ResponseEntity<>(statements, HttpStatus.OK);
	}

	//if both of the dates are provided
	/**
	 * @param token
	 * @param from
	 * @param to
	 * @return ResponseEntity<List<Statement>>
	 * @throws ParseException
	 */

	//Rest end point to get Account statement between two accounts
	@GetMapping("/getAccountStatement/{accountId}/{from}/{to}")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Map<String,Object>> getAccountStatement(@RequestHeader("Authorization") String token,@PathVariable long accountId,@PathVariable String from, @PathVariable String to) throws ParseException{
		accountServiceImpl.hasAccountAccess(token,accountId);
		Map<String,Object> statements = accountServiceImpl.getAccountStatement(accountId,from,to);
		Account account = accountRepository.findByAccountId(accountId);
		statements.put("balance",account.getCurrentBalance());
		LOGGER.info("Account Statement List Returned");
		return new ResponseEntity<>(statements, HttpStatus.OK);
	}



	/**
	 * @param token
	 * @param accInput
	 * @return  ResponseEntity<Account>
	 */

	//Rest end point to deposit amount into account
	@PostMapping("/deposit")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Account> deposit(@RequestHeader("Authorization") String token,@RequestBody AccountInput accInput) {
		accountServiceImpl.hasPermission(token);

		// Calling the makeDeposit API from Transaction microservice
		transactionFeign.makeDeposit(token, accInput);
		Account newUpdateAccBal = accountServiceImpl.updateDepositBalance(accInput);
		List<Transaction> list = transactionFeign.getTransactionsByAccId(token, accInput.getAccountId());
		newUpdateAccBal.setTransactions(list);
		LOGGER.info("amount deposited");
		return new ResponseEntity<>(newUpdateAccBal, HttpStatus.OK);
	}



	/**
	 * @param token
	 * @param accInput
	 * @return ResponseEntity<Account>
	 */

	// Rest end to withdraw amount from the account
	@PostMapping("/withdraw")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<Account> withdraw(@RequestHeader("Authorization") String token, @RequestBody AccountInput accInput) {
		accountServiceImpl.hasPermission(token);
		try {
			// Calling the makeWithdraw API from Transaction microservice
			transactionFeign.makeWithdraw(token, accInput);

		} catch (Exception e) {

			//Account should contain a minimum balance of 1000.Rs
			LOGGER.error("Minimum Balance 1000 should be maintaind");
			throw new MinimumBalanceException("Minimum Balance 1000 should be maintained");
		}
		Account newUpdateAccBal = accountServiceImpl.updateBalance(accInput);
		List<Transaction> list = transactionFeign.getTransactionsByAccId(token, accInput.getAccountId());
		newUpdateAccBal.setTransactions(list);
		LOGGER.info("amount withdraw sucessful");
		return new ResponseEntity<>(newUpdateAccBal, HttpStatus.OK);
	}


	// to calculate service charge
	/**
	 * @param token
	 * @param accInput
	 * @return ResponseEntity<Account>
	 */
	@PostMapping("/servicecharge")
	public ResponseEntity<Account> servicecharge(@RequestHeader("Authorization") String token,@RequestBody AccountInput accInput) {
		accountServiceImpl.hasPermission(token);
		try {
			transactionFeign.makeServiceCharges(token, accInput);

		} catch (Exception e) {
			LOGGER.error("Minimum Balance 1000 should be maintained");
			throw new MinimumBalanceException("Minimum Balance 1000 should be maintaind");
		}
		Account newUpdateAccBal = accountServiceImpl.updateBalance(accInput);
		List<Transaction> list = transactionFeign.getTransactionsByAccId(token, accInput.getAccountId());
		newUpdateAccBal.setTransactions(list);
		LOGGER.info("amount service charged sucessful");
		return new ResponseEntity<>(newUpdateAccBal, HttpStatus.OK);
	}


	//make transaction from one account to another
	/**
	 * @param token
	 * @param transInput
	 * @return ResponseEntity<String>
	 */
	@PostMapping("/transaction")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<?> transaction(@RequestHeader("Authorization") String token, @RequestBody TransactionInput transInput) {
		accountServiceImpl.hasPermission(token);
		boolean status = true;
		try {
			status = transactionFeign.makeTransfer(token, transInput);

		} catch (Exception e) {
			LOGGER.error("Minimum Balance 1000 should be maintaind");
			throw new MinimumBalanceException("Minimum Balance 1000 should be maintained");
		}
		if (status == false) {
			return new ResponseEntity<>("Transaction Failed", HttpStatus.NOT_IMPLEMENTED);
		}
		Account updatedSourceAccBal = accountServiceImpl.updateBalance(transInput.getSourceAccount());
		List<Transaction> sourceAcc = transactionFeign.getTransactionsByAccId(token,transInput.getSourceAccount().getAccountId());
		updatedSourceAccBal.setTransactions(sourceAcc);

		Account updatedTargetAccBal = accountServiceImpl.updateDepositBalance(transInput.getTargetAccount());
		List<Transaction> targetAcc = transactionFeign.getTransactionsByAccId(token,transInput.getTargetAccount().getAccountId());
		updatedTargetAccBal.setTransactions(targetAcc);
		//Updating the account statement
		LOGGER.info("Transaction completed successfully from AccId" + transInput.getSourceAccount().getAccountId()+ " TO Target AccId " + transInput.getTargetAccount().getAccountId());
		messageDetails.setReason("Transfer is Successful for Rs."+transInput.getAmount()  +" from A/C no. "+transInput.getSourceAccount().getAccountId()+" to A/C no. "+transInput.getTargetAccount().getAccountId());
		return new ResponseEntity<>(messageDetails,HttpStatus.OK);
	}



	/**
	 * @param token
	 * @param accountInput
	 * @return  ResponseEntity<Account>
	 */
	@PostMapping("/checkBalance")
	public ResponseEntity<Account> checkAccountBalance(@RequestHeader("Authorization") String token,@Valid @RequestBody AccountInput accountInput) {
		accountServiceImpl.hasPermission(token);
		Account account = accountServiceImpl.getAccount(accountInput.getAccountId());
		return new ResponseEntity<>(account, HttpStatus.OK);
	}


	//all accounts
	/**
	 * @param token
	 * @return ResponseEntity<List<Account>>
	 */
	@GetMapping("/find")
	public ResponseEntity<List<Account>> getAllAccount(@RequestHeader("Authorization") String token) {
		accountServiceImpl.hasPermission(token);
		List<Account> account = accountServiceImpl.getAllAccounts();
		return new ResponseEntity<>(account, HttpStatus.OK);
	}
	@DeleteMapping("deleteCustomer/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> deleteCustomer(@RequestHeader("Authorization") String token, @PathVariable String id) {

		System.out.println("Starting deletion of account " + id);
		accountServiceImpl.deleteCustomer(id);
		System.out.println("Deleted");
		return new ResponseEntity<>("Account Deleted successfully", HttpStatus.OK);
	}

}

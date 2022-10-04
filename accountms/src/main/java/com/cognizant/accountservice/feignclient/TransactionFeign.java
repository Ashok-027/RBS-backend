package com.cognizant.accountservice.feignclient;

import com.cognizant.accountservice.model.Account;
import com.cognizant.accountservice.model.AccountInput;
import com.cognizant.accountservice.model.Transaction;
import com.cognizant.accountservice.model.TransactionInput;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.text.ParseException;
import java.util.List;
import java.util.Map;


@FeignClient(name = "transaction-ms", url = "${feign.url-transaction-service}")
public interface TransactionFeign {

	//deposit
	/**
	 * @param token
	 * @param accountInput
	 * @return ResponseEntity<>
	 */
	@PostMapping("/deposit")
	public ResponseEntity<?> makeDeposit(@RequestHeader("Authorization") String token,@Valid @RequestBody AccountInput accountInput);

	// withdraw
	/**
	 * @param token
	 * @param accountInput
	 * @return boolean
	 */
	@PostMapping("/withdraw")
	public boolean makeWithdraw(@RequestHeader("Authorization") String token, @Valid @RequestBody AccountInput accountInput) ;

	//check balance 
	
	//@JSONRequestMapping(value = "/account", method = RequestMethod.POST)
	/**
	 * @param accountInput
	 * @return Account
	 */
	@PostMapping(value = "/accounts", consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
	public Account checkAccountBalance(@Valid @RequestBody AccountInput accountInput);

	//transfer amount from one account to another
	/**
	 * @param token
	 * @param transactionInput
	 * @return boolean
	 */
	@PostMapping(value = "/transactions")
	public boolean makeTransfer(@RequestHeader("Authorization") String token,@Valid @RequestBody TransactionInput transactionInput);
	
	//trans. by account
	/**
	 * @param token
	 * @param accId
	 * @return List<Transaction>
	 */
	@GetMapping(value = "/getAllTransByAccId/{id}")
	public List<Transaction> getTransactionsByAccId(@RequestHeader("Authorization") String token, @PathVariable("id") long accId);
	
	//service charges by account
	/**
	 * @param token
	 * @param accountInput
	 * @return boolean
	 */
	@PostMapping(value = "/servicecharge")
	public boolean makeServiceCharges(@RequestHeader("Authorization") String token,@Valid @RequestBody AccountInput accountInput);

	@GetMapping("/getAccountStatement/{accountId}")
	public Map<String,Object> getAccountStatement(@PathVariable long accountId);

	@GetMapping("/getAccountStatement/{accountId}/{from}/{to}")
	public Map<String,Object> getAccountStatement(@PathVariable long accountId,@PathVariable String from, @PathVariable String to) throws ParseException;


}

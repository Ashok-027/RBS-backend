package com.cognizant.transactionservice.controller;

import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.cognizant.transactionservice.feign.AccountFeign;
import com.cognizant.transactionservice.feign.RulesFeign;
import com.cognizant.transactionservice.models.Transaction;
import com.cognizant.transactionservice.repository.TransactionRepository;
import com.cognizant.transactionservice.service.TransactionServiceInterface;
import com.cognizant.transactionservice.util.AccountInput;
import com.cognizant.transactionservice.util.TransactionInput;

import lombok.extern.slf4j.Slf4j;


@RestController
@Slf4j
public class TransactionRestController {


	@Autowired
	AccountFeign accountFeign;

	@Autowired
	RulesFeign rulesFeign;

	@Autowired
	TransactionRepository transRepo;

	@Autowired
	TransactionServiceInterface transactionService;

	
	/**
	 * @param token
	 * @param transactionInput
	 * @return boolean
	 */
	@PostMapping(value = "/transactions")
	public boolean makeTransfer(@RequestHeader("Authorization") String token,
			@Valid @RequestBody TransactionInput transactionInput) {
		log.info("inside transaction method");
		if (transactionInput != null) {
			boolean isComplete = transactionService.makeTransfer(token, transactionInput);
			
			return isComplete;
		} else {
			return false;
		}
	}




	/**
	 * @param token
	 * @param accId
	 * @return List<Transaction>
	 */
	@GetMapping(value = "/getAllTransByAccId/{id}")
	@CrossOrigin
	public List<Transaction> getTransactionsByAccId(@RequestHeader("Authorization") String token,
			@PathVariable("id") long accId) {
		List<Transaction> slist = transRepo.findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(accId, accId);
		return slist;
	}


	/**
	 * @param token
	 * @param accountInput
	 * @return boolean
	 */
	
	//Rest end point which implements the withdraw service
	@PostMapping(value = "/withdraw")
	public boolean makeWithdraw(@RequestHeader("Authorization") String token,
			@Valid @RequestBody AccountInput accountInput){
		transactionService.makeWithdraw(token, accountInput);
		return true;
	}
	
	/**
	 * @param token
	 * @param accountInput
	 * @return boolean
	 */
	
	//function to apply service charges 
	@PostMapping(value = "/servicecharge")
	public boolean makeServiceCharges(@RequestHeader("Authorization") String token,@Valid @RequestBody AccountInput accountInput) {
		transactionService.makeServiceCharges(token, accountInput);
		return true;
	}

	


	
	/**
	 * @param token
	 * @param accountInput
	 * @return ResponseEntity<?>
	 */
	@PostMapping(value = "/deposit")
	public ResponseEntity<?> makeDeposit(@RequestHeader("Authorization") String token,
			@Valid @RequestBody AccountInput accountInput) {
		transactionService.makeDeposit(token, accountInput);
		return new ResponseEntity<>(true, HttpStatus.OK);
	}

	@GetMapping("/getAccountStatement/{accountId}")
	public Map<String,Object> getAccountStatement(@PathVariable long accountId){
//		accountServiceImpl.hasPermission(token);
		List<Transaction> statements = transactionService.getAccountStatement(accountId);
//		LOGGER.info("Account Statement List Returned");
		int count = transRepo.countTransactionBySourceAccountIdOrTargetAccountId(accountId,accountId);
		Map<String,Object> map = new HashMap<>();
		map.put("accountId",accountId);
		map.put("transactionsCount",count);
		map.put("transactions",statements);
		return map;
	}

	//if both of the dates are provided


	//Rest end point to get Account statement between two accounts
	@GetMapping("/getAccountStatement/{accountId}/{from}/{to}")
	public Map<String,Object> getAccountStatement(@PathVariable long accountId,@PathVariable String from, @PathVariable String to) throws ParseException {
//		accountServiceImpl.hasPermission(token);
		List<Transaction> statements = transactionService.getAccountStatement(accountId,from,to);
//		LOGGER.info("Account Statement List Returned");
		int count = transRepo.countTransactionBySourceAccountIdOrTargetAccountId(accountId,accountId);
		Map<String,Object> map = new HashMap<>();
		map.put("accountId",accountId);
		map.put("transactionsCount",count);
		map.put("transactions",statements);
		return map;
	}


}

package com.cognizant.accountservice.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.cognizant.accountservice.model.Account;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {
	
	/**
	 *  Class used Account Repository
	 */
	
	/*
	 *  method to get account from database
	 */
	/**
	 * @param accountId
	 * @return Account
	 */
	@Query(nativeQuery = true, value = "SELECT * from account a WHERE a.account_Id = :accountId")
	Account findByAccountId(@Param(value = "accountId") long accountId);

	@Query(nativeQuery = true, value = "SELECT customer_id from account a WHERE a.account_Id = :accountId")
	String findCustomerId(long accountId);
	/*
	 *  method to find customer from database
	 */
	/**
	 * @param customerId
	 * @return List<Account>
	 */
	List<Account> findByCustomerId(String customerId);


}

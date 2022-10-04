package com.cognizant.transactionservice.repository;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cognizant.transactionservice.models.Transaction;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface TransactionRepository extends JpaRepository<Transaction, Long> {

	
    /**
     * @param id
     * @param id2
     * @return List<Transaction>
     */
    List<Transaction> findBySourceAccountIdOrTargetAccountIdOrderByInitiationDate(long id , long id2);

	/**
	 * @param accId
	 * @return List<Transaction>
	 */
	List<Transaction> findByTargetAccountIdOrderByInitiationDate(long accId);

	/**
	 * @param i
	 * @return List<Transaction>
	 */
	List<Transaction> findBySourceAccountIdOrderByInitiationDate(int i);

	Integer countTransactionBySourceAccountIdOrTargetAccountId(long id,long id2);

	@Query(nativeQuery = true, value = "SELECT * from transaction t WHERE (t.source_account_id = :accountId or t.target_account_id = :accountId) and (t.initiation_date between :startDate and :endDate) order by t.initiation_date desc ")
	List<Transaction> findStatementByAccountId(@Param(value = "accountId") long accountId, Date startDate, Date endDate);

}

package com.cognizant.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.Date;
import java.util.List;


@Entity
@Table(name = "account")
@NoArgsConstructor
@AllArgsConstructor 
public class Account {

	// Account model
	@Id
	@Getter
	@Setter 
	@Column(length=10)
	@SequenceGenerator(name="seq", initialValue = 1000000001)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator="seq")
	private long accountId;
	
	@NotBlank(message = "Enter customerId")
	@Getter
	@Setter
	private String customerId;

	@NotNull(message = "Enter currentBalance")
	@Getter
	@Setter
	private double currentBalance;

	@Getter
	@Setter
	@NotBlank(message = "Enter accountType")
	private String accountType;

	@Getter
	@Setter
	private Date openingDate;

	
	@Getter
	@Setter
	@Column(length = 20)
	private String ownerName;

	//one customer has multiple account
	//one account has multiple transactions
	// mapping of account and transaction is done by account object in transaction model
	//@OneToMany(mappedBy="account")
	//private List<Transaction> transactions;
	
	// mapping of account and customer is done by customer object
	//@ManyToOne
	//private Customer customer;
	//not mapped to any database column
	@Getter
	@Setter
	@Transient
	private List<Transaction> transactions;
	
	@Override
	public String toString() {
		return "Account information : [accountId=" + accountId + ", openingDate=" + openingDate + ", currentBalance=" + currentBalance
				+ ", accountType=" + accountType + "]";
	}

}
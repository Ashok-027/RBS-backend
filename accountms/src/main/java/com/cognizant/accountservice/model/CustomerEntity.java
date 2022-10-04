package com.cognizant.accountservice.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Table;
import java.sql.Date;


@Table
@NoArgsConstructor
@AllArgsConstructor
public class CustomerEntity {
	@Getter
	@Setter
	private String userid;
	@Getter
	@Setter
	private String username;
	@Getter
	@Setter
	private String password;
	@Getter
	@Setter
	private Date dateOfBirth;
	@Getter
	@Setter
	private String pan;
	@Getter
	@Setter
	private String address;

}
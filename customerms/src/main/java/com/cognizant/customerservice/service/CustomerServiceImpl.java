package com.cognizant.customerservice.service;

import java.util.List;
import java.util.Optional;

import com.cognizant.customerservice.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cognizant.customerservice.exception.AccessDeniedException;
import com.cognizant.customerservice.feign.AccountFeign;
import com.cognizant.customerservice.feign.AuthorizationFeign;
import com.cognizant.customerservice.repository.CustomerRepository;

import lombok.extern.slf4j.Slf4j;


@Slf4j
@Service
public class CustomerServiceImpl implements CustomerService {

	private static final String CUSTOMER = "CUSTOMER";
	@Autowired
	AuthorizationFeign authorizationFeign;

	@Autowired
	AccountFeign accountFeign;

	@Autowired
	CustomerRepository customerRepo;


	@Autowired
	MessageDetails messageDetails;

	@Override
	public AuthenticationResponse hasPermission(String token) {
		return authorizationFeign.getValidity(token);
	}

	
	//Function which check's if the customer is authorization to access the resource
	@Override
	public AuthenticationResponse hasEmployeePermission(String token) {
		AuthenticationResponse validity = authorizationFeign.getValidity(token);
		if (!authorizationFeign.getRole(validity.getUserid()).equals("EMPLOYEE"))
			throw new AccessDeniedException("NOT ALLOWED");
		else
			return validity;
	}

	//Function which check's if the Customer is authorization to access the resource
	@Override
	public AuthenticationResponse hasCustomerPermission(String token) {
		
		//check if the token present in the header is valid or not
		AuthenticationResponse validity = authorizationFeign.getValidity(token);
		
		if (!authorizationFeign.getRole(validity.getUserid()).equals(CUSTOMER))
			throw new AccessDeniedException("NOT ALLOWED");
		else
			return validity;
	}

	
	//Function to create customer and account for the customer  
	@Override
	public MessageDetails createCustomer(String token, CustomerEntity customer) {

		CustomerEntity checkCustomerExists = getCustomerDetail(token, customer.getUserid());
		if (checkCustomerExists == null) {
			AppUser user = new AppUser(customer.getUserid(), customer.getUsername(), customer.getPassword(), null,
					CUSTOMER);
			//Calls the create user service from Authentication Microservice using feign client 
			authorizationFeign.createUser(user);
		CustomerEntity newCustomer = new CustomerEntity();
		newCustomer.setUserid(customer.getUserid());
		newCustomer.setUsername(customer.getUsername());
		newCustomer.setPan(customer.getPan());
		newCustomer.setAddress(customer.getAddress());
		newCustomer.setDateOfBirth(customer.getDateOfBirth());
		newCustomer.setPassword(customer.getPassword());
		customerRepo.save(newCustomer);
		Account acc = new Account();
		acc.setCustomerId(customer.getUserid());
		acc.setAccountType("Saving");
		acc.setCurrentBalance(1000);
		AccountCreationStatus status = accountFeign.createAccount(token, customer.getUserid(), acc);
		log.info("Consumer details saved.");
		messageDetails.setReason("Customer "+newCustomer.getUserid()+" with A/C no. "+status.getAccountId()+" is created Successfully");
		return messageDetails;
		}
		else{
			messageDetails.setReason("Customer with customer-id already exist.");
			return messageDetails;
		}

	}

	
	//Function to get Customer details based on Customer ID
	@Override
	public CustomerEntity getCustomerDetail(String token, String id) {
		Optional<CustomerEntity> customer = customerRepo.findById(id);
		if (!customer.isPresent())
			return null;
		log.info("Customer details fetched.");
		List<Account> list = accountFeign.getCustomerAccount(token, id);
		customer.get().setAccounts(list);
		return customer.get();
	}
	
	
	
	//function to delete customer detail based on customer Id
	@Override
	public boolean deleteCustomer(String id) {
		CustomerEntity customer = customerRepo.findById(id).get();
		if (customer != null)
			customerRepo.deleteById(id);
		else
			return false;
		log.info("Consumer details deleted.");
		return true;
	}

	//function to save customer details
	@Override
	public CustomerEntity saveCustomer(String token, CustomerEntity customer) {
		CustomerEntity checkCustomerExists = getCustomerDetail(token, customer.getUserid());
		if (checkCustomerExists == null) {
			AppUser user = new AppUser(customer.getUserid(), customer.getUsername(), customer.getPassword(), null,
					CUSTOMER);
			authorizationFeign.createUser(user);
		}
		return customerRepo.save(customer);
	}
	@Override
	public List<CustomerEntity> getAllCustomers(){
		List<CustomerEntity> customers = customerRepo.findAll();
		return customers;

	}

	//function to update customer detail
	@Override
	public CustomerEntity updateCustomer(String token, updateCustomerInput customer) {
//		CustomerEntity toUpdate = customerRepo.findById(customer.getUserid()).get();
//		toUpdate.setAccounts(customer.getAccounts());
//		for (Account acc : customer.getAccounts()) {
//			accountFeign.createAccount(token, customer.getUserid(), acc);
//		}
//		return customerRepo.save(toUpdate);
//		for (Account acc : customer.getAccounts()) {
//			accountFeign.createAccount(token, customer.getUserid(), acc);
//		}
		CustomerEntity toUpdate = customerRepo.findById(customer.getUserid()).get();
		toUpdate.setUsername(customer.getUsername());
		toUpdate.setAddress(customer.getAddress());
		toUpdate.setPan(customer.getPan());
		toUpdate.setDateOfBirth(customer.getDateOfBirth());
		return customerRepo.save(toUpdate);
	}

}

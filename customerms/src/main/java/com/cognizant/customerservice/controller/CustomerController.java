package com.cognizant.customerservice.controller;

import java.net.BindException;
import java.time.DateTimeException;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import com.cognizant.customerservice.model.MessageDetails;
import com.cognizant.customerservice.model.updateCustomerInput;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.cognizant.customerservice.feign.AuthorizationFeign;
import com.cognizant.customerservice.model.CustomerEntity;
import com.cognizant.customerservice.service.CustomerService;


@RestController
public class CustomerController {  // /customer endpoint

	@Autowired
	private CustomerService customerService;  //customer interface

	@Autowired
	AuthorizationFeign authorizationFeign;   //auth feign


	@Autowired
	MessageDetails messageDetails;

	/**
	 * @param token
	 * @param customer

	 * @return ResponseEntity<?>
	 * @throws DateTimeException
	 * @throws BindException
	 * @apiNote creates the customer 
	 */
	@PostMapping("/createCustomer")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<?> createCustomer(@RequestHeader("Authorization") String token, @Valid @RequestBody CustomerEntity customer , BindingResult bindingResult) throws DateTimeException, BindException {

		//create customer service from customer service class
		MessageDetails response = new MessageDetails();
		try {
			//checks if the customer input's are valid
			if (bindingResult.hasErrors()) {
				throw new BindException();
			}
			//function to check if employee is valid to create a customer
			customerService.hasEmployeePermission(token);
			response = customerService.createCustomer(token, customer);
		} catch (Exception e) {
			response.setReason("Customer Creation is UNSUCCESSFUL");
			return new ResponseEntity<>(response, HttpStatus.NOT_ACCEPTABLE);
		}
		return new ResponseEntity<>(response, HttpStatus.CREATED);
	}
	
	/**
	 * @param token
	 * @param customer
	 * @return CustomerEntity
	 * @apiNote Save customer into the database
	 */
	@PostMapping("/saveCustomer")
	public CustomerEntity saveCustomer(@RequestHeader("Authorization") String token,@Valid @RequestBody CustomerEntity customer) {
		customerService.hasEmployeePermission(token);
		CustomerEntity customerEntity = customerService.saveCustomer(token,customer);
		if (customerEntity != null)
			return customerEntity;
		else
			return null;
	}
	
	/**
	 * @param token
	 * @param customer
	 * @return CustomerEntity
	 */ 
	@PostMapping("/updateCustomer")
	@CrossOrigin(origins = "http://localhost:3000")
	public ResponseEntity<?> updateCustomer(@RequestHeader("Authorization") String token,@Valid @RequestBody updateCustomerInput customer) {
		customerService.hasEmployeePermission(token);
		CustomerEntity updatedCustomerEntity = customerService.updateCustomer(token,customer);
		if(updatedCustomerEntity.getUserid()!=null){
			messageDetails.setReason("Updated Successfully!");
			return new ResponseEntity<>(messageDetails,HttpStatus.OK);
		}else{
			messageDetails.setReason("Update Failed");
			return new ResponseEntity<>(messageDetails,HttpStatus.NOT_ACCEPTABLE);
		}

	}
	 
	/**
	 * @param token
	 * @param id
	 * @return ResponseEntity<?>
	 */
	@CrossOrigin(origins = "http://localhost:3000")
	@GetMapping("/getCustomerDetails/{id}")
	public ResponseEntity<?> getCustomerDetails(@RequestHeader("Authorization") String token, @PathVariable String id) {
		customerService.hasPermission(token);
		CustomerEntity toReturnCustomerDetails = customerService.getCustomerDetail(token,id);
		if (toReturnCustomerDetails == null)
			return new ResponseEntity<>("Customer Userid "+id+" DOES NOT EXISTS", HttpStatus.NOT_ACCEPTABLE);
		toReturnCustomerDetails.setPassword(null);
		return new ResponseEntity<>(toReturnCustomerDetails, HttpStatus.OK);
	}

	/**
	 * @param token
	 * @param id
	 * @return ResponseEntity<?>
	 */
	@CrossOrigin(origins = "http://localhost:3000")
	@DeleteMapping("deleteCustomer/{id}")
	@ResponseStatus(code = HttpStatus.OK)
	public ResponseEntity<?> deleteCustomer(@RequestHeader("Authorization") String token, @PathVariable String id) {

		customerService.hasEmployeePermission(token);

		CustomerEntity checkCustomerIdExists = null;
		checkCustomerIdExists = customerService.getCustomerDetail(token , id);
		if (checkCustomerIdExists == null) {
			return new ResponseEntity<>("Customer Userid DOES NOT EXISTS", HttpStatus.NOT_ACCEPTABLE);
		}

		
		customerService.deleteCustomer(id);
		System.out.println("Deleted");
		return new ResponseEntity<>("Deleted SUCCESSFULLY", HttpStatus.OK);
	}

	/**
	 * @param token
	 * @return String
	 */
	@GetMapping("/check") 
	public String checkAccessWWithoutValidation(@RequestHeader("Authorization") String token) {
		customerService.hasEmployeePermission(token);
		return "Your Token is valid";
	}

	@GetMapping("/getallcustomers")
	@CrossOrigin(origins = "http://localhost:3000")
	public List<HashMap<String,Object>> getAllCustomers(@RequestHeader("Authorization") String token){
		customerService.hasEmployeePermission(token);
		List<CustomerEntity> customers = customerService.getAllCustomers();

		List<HashMap<String,Object>> customerlist=customers.stream().map(customerEntity -> {
			HashMap<String,Object> customer=new HashMap<>();
			customer.put("userid",customerEntity.getUserid());
			customer.put("username",customerEntity.getUsername());
			customer.put("address",customerEntity.getAddress());
			return customer;
		}).collect(Collectors.toList());

		return customerlist;
	}



}
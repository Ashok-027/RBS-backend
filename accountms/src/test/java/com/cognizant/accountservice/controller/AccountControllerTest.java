package com.cognizant.accountservice.controller;

import static org.mockito.Mockito.timeout;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.cognizant.accountservice.feignclient.CustomerFeignProxy;
import com.cognizant.accountservice.model.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.cognizant.accountservice.feignclient.AuthFeignClient;
import com.cognizant.accountservice.feignclient.TransactionFeign;
import com.cognizant.accountservice.repository.AccountRepository;
import com.cognizant.accountservice.service.AccountServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(controllers = AccountController.class)
@ExtendWith(SpringExtension.class)
class AccountControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockBean
	private MessageDetails messageDetails;

	@MockBean
	private AuthFeignClient authFeign;

	@MockBean
	private AccountServiceImpl accountServiceImpl;

	@MockBean
	CustomerFeignProxy customerFeignProxy;

	@MockBean
	private AccountRepository accountRepository;

	@MockBean
	private TransactionFeign transactionFeign;
	

	@Test
	void getAccountTest() throws Exception {
		when(accountServiceImpl.hasPermission("token")).thenReturn(new AuthenticationResponse("", "", true));
		Account acc = new Account();
		when(accountServiceImpl.getAccount(1)).thenReturn(acc);
		mockMvc.perform(get("/getAccount/1").header("Authorization", "token")).andExpect(status().isOk());
		verify(accountServiceImpl, timeout(1)).getAccount(1);
	}

	@Test
	void getCustomerAccountTest() throws Exception {
		when(accountServiceImpl.hasPermission("token")).thenReturn(new AuthenticationResponse("", "", true));
		when(accountServiceImpl.getCustomerAccount("token", "cust01")).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/getAccounts/cust01").header("Authorization", "token")).andExpect(status().isOk());
		verify(accountServiceImpl, timeout(1)).getCustomerAccount("token", "cust01");
	}

	@Test
	void createAccountTest() throws Exception {
		Date date = new Date();
		java.sql.Date sqlDate = new java.sql.Date(date.getTime());
		when(accountServiceImpl.hasEmployeePermission("token")).thenReturn(new AuthenticationResponse("emp01", "emp", true));
		Account account = new Account(1l, "cust01", 3000.0, "Savings", date, "sid", null);
		when(accountRepository.save(account)).thenReturn(account);
		when(customerFeignProxy.getCustomerDetails("token","Cust01")).thenReturn(new CustomerEntity("Cust01","sid","admin",sqlDate,"SEROP8453I","kol"));
		when(accountServiceImpl.createAccount("token","cust101", account)).thenReturn(new AccountCreationStatus(account.getAccountId(), "A/C no. "+account.getAccountId()+" is created Successfully"));

		mockMvc.perform(MockMvcRequestBuilders
		.post("/createAccount/cust01")
		.content(asJsonString(account))
		.contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON)
		.header("Authorization", "token")).andExpect(status().isNotAcceptable());
		verify(accountServiceImpl, timeout(1)).hasEmployeePermission("token");
	}

	/*@Test
	void checkAccountBalanceTest() throws Exception {
		when(accountServiceImpl.hasPermission("token")).thenReturn(new AuthenticationResponse("cust01", "cust", true));
		AccountInput accountIp = new AccountInput();
		Account account = new Account();
		when(accountServiceImpl.getAccount(accountIp.getAccountId())).thenReturn(account);
		mockMvc.perform(MockMvcRequestBuilders.post("/checkBalance")
		.content(asJsonString(accountIp))
		.contentType(MediaType.APPLICATION_JSON)
		.accept(MediaType.APPLICATION_JSON)
		.header("Authorization", "token")).andExpect(status().isOk());
		verify(accountServiceImpl, timeout(1)).hasPermission("token");
	}
	*/
	@Test
	void  getAllAccountTest() throws Exception  {
		when(accountServiceImpl.hasPermission("token")).thenReturn(new AuthenticationResponse("", "", true));
		when(accountServiceImpl.getAllAccounts()).thenReturn(new ArrayList<>());
		mockMvc.perform(get("/find").header("Authorization", "token")).andExpect(status().isOk());
		verify(accountServiceImpl, timeout(1)).getAllAccounts();
	}


	// converting object to string
	/**
	 * @param obj
	 * @return String
	 */
	public static String asJsonString(final Object obj) {
		try {
			final ObjectMapper mapper = new ObjectMapper();
			final String jsonContent = mapper.writeValueAsString(obj);
			return jsonContent;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	// https://howtodoinjava.com/spring-boot2/testing/spring-boot-mockmvc-example/
}

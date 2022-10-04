package com.cognizant.authenticationservice.service;

import com.cognizant.authenticationservice.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Component;

import com.cognizant.authenticationservice.exceptionhandling.AppUserNotFoundException;
import com.cognizant.authenticationservice.model.AppUser;

import lombok.extern.slf4j.Slf4j;

import java.util.NoSuchElementException;


@Component
@Slf4j
public class LoginService {

	@Autowired
	private JwtUtil jwtutil;
	@Autowired
	private BCryptPasswordEncoder encoder;
	
	@Autowired
	private CustomerDetailsService customerDetailservice;

	@Autowired
	private UserRepository userRepository;

	/**
	 * @param appuser
	 * @return AppUser
	 * @throws AppUserNotFoundException
	 */
	public AppUser userLogin(AppUser appuser) throws AppUserNotFoundException {
		UserDetails userdetails;
		try {
			userdetails = customerDetailservice.loadUserByUsername(appuser.getUserid());
		} catch (NoSuchElementException e) {
			throw new AppUserNotFoundException("User Doesn't exist.");
		}
		String userid = "";
		String role = "";
		String token = "";

		log.info("LOGGING IN WITH USERNAME AND PASSWORD");
		if (userdetails.getPassword().equals(appuser.getPassword())) {
			userid = appuser.getUserid();
			token = jwtutil.generateToken(userdetails);
			role = userRepository.findById(userid).get().getRole();
			return new AppUser(userid, null, null, token, role);
		} else {
			throw new AppUserNotFoundException("Username/Password is incorrect...Please check");
		}
	}
}
package com.cognizant.authenticationservice.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.cognizant.authenticationservice.model.AppUser;



@Repository
public interface UserRepository extends JpaRepository<AppUser, String> {
    @Query(nativeQuery = true, value = "SELECT role from appuser a WHERE a.userid = :userid")
    String findRoleById(String userid);
}
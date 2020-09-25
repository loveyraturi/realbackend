package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.AdminUser;
import com.praveen.model.Interested;
import com.praveen.model.Users;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface AdminUserRepository extends JpaRepository<AdminUser, Integer> {
	@Query(value="SELECT * FROM admin_users where username=:username and password=:password", nativeQuery = true)
	AdminUser validateUser(@Param("username") String username,@Param("password") String password);	
}

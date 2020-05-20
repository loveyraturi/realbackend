package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveen.model.Campaing;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

public interface UserGroupRepository extends JpaRepository<UserGroup, Integer> {

}

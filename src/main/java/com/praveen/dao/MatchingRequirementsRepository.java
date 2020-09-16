package com.praveen.dao;

import java.sql.Timestamp;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.Images;
import com.praveen.model.Interested;
import com.praveen.model.MatchingRequirements;
@Repository
@Transactional
public interface MatchingRequirementsRepository extends JpaRepository<MatchingRequirements, Integer> {
	 @Query(value="SELECT * FROM matching_requirements where username=:username", nativeQuery = true)
	 MatchingRequirements findByUsername(@Param("username") String username);
	
}

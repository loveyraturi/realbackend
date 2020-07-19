package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;

import com.praveen.model.LeadVersions;

public interface LeadVersionsRepository extends JpaRepository<LeadVersions, Integer> {

}

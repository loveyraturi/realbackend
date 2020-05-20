package com.praveen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;

public interface GroupCampaingMappingRepository extends JpaRepository<GroupCampaingMapping, Integer> {
	
}

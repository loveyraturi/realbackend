package com.praveen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

public interface CampaingRepository extends JpaRepository<Campaing, Integer> {
	@Query(value="select campaing.* from campaing INNER JOIN group_campaing_mapping as table1 ON table1.campaingname = campaing.name where table1.groupname=:groupname and table1.campaingname=:campaing ", nativeQuery = true)
	 List<Campaing> findCampaingByGroupName(@Param("groupname") String groupname,@Param("campaing") String campaing);
	@Query(value="select * from campaing  where name=:name ", nativeQuery = true)
	 Campaing findCampaingByName(@Param("name") String name);
}

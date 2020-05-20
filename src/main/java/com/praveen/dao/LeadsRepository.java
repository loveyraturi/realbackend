package com.praveen.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Campaing;
import com.praveen.model.Leads;

public interface LeadsRepository extends JpaRepository<Leads, Integer> {
	@Query(value="select leads.* from leads INNER JOIN campaing_lead_mapping as table1 ON table1.leadid = leads.id where table1.campaingid=:campaingid and (status='ACTIVE' OR status='CALL BACK' OR status='SWITCH OFF' OR status='BUSY' OR status='NOT RECHABLE') LIMIT 1", nativeQuery = true)
	 List<Leads> findLeadsByCampaingId(@Param("campaingid") int campaingid);
	@Query(value="select * from leads  where first_name=:firstname ", nativeQuery = true)
	 Leads findLeadByFirstName(@Param("firstname") String firstname);
}

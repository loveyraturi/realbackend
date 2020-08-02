package com.praveen.dao;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Break;
import com.praveen.model.Campaing;
import com.praveen.model.Recordings;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

public interface RecordingRepository extends JpaRepository<Recordings, Integer> {	
	@Query(value="select * from recordings  where username=:username ", nativeQuery = true)
	 List<Recordings> fetchRecordingsByUsername(@Param("username") String username);
	@Query(value="select * from recordings  where recording_name=:filename ", nativeQuery = true)
	 Recordings fetchRecordingsByFilename(@Param("filename") String filename);
	@Query(value="select table1.assigned_to,table1.call_end_date,table1.call_date,table1.status,table1.last_local_call_time,recordings.recording_name,table1.phone_number,table1.filename,recordings.campaing from recordings INNER JOIN leads as table1 ON table1.id = recordings.lead_id where recordings.lead_id IN (:leadId) ", nativeQuery = true)
	List<Object[]> fetchRecordingsByLeadIds(@Param("leadId") List<Integer> leadId);
}

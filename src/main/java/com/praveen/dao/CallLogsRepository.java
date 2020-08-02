package com.praveen.dao;

import java.util.Date;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.CallLogs;
import com.praveen.model.Campaing;
import com.praveen.model.Leads;

public interface CallLogsRepository extends JpaRepository<CallLogs, Integer> {
	@Query(value="select assigned_to,phone_number,status,username,first_name,comment,call_duration,call_date,call_end_date from call_logs INNER JOIN leads as table1 ON table1.id = call_logs.lead_id where  call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and id IN (:leadIds) ", nativeQuery = true)
	 List<Object[]> fetchreportdatabetween(@Param("leadIds") List<Integer> leadIds, @Param("fromDate") String fromDate,@Param("toDate") String toDate);
	@Query(value="select assignedTo,status,count(status) as count from call_logs where call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and lead_id IN (:leadIds) GROUP BY name,status", nativeQuery = true)
	 List<Object[]> fetchcountreportdatabetween(@Param("leadIds") List<Integer> leadIds, @Param("fromDate") String fromDate,@Param("toDate") String toDate);
	 @Query(value="select status,count(status) as count from call_logs where call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS') and assigned_to=:userName GROUP BY status", nativeQuery = true)
	 List<Object[]> fetchcountreportdatabetweenWithUsers(@Param("userName") String userName, @Param("fromDate") String fromDate,@Param("toDate") String toDate); 
	 @Query(value="select * from call_logs where assigned_to=:userName and status!='ACTIVE'", nativeQuery = true)
	 List<CallLogs> fetchreportdatabetweenWithUserName(@Param("userName") String userName);	
}

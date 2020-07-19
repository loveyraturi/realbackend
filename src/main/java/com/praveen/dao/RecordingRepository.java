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
}

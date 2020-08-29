package com.praveen.dao;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Images;
import com.praveen.model.Interested;

public interface InterestedRepository extends JpaRepository<Interested, Integer> {
	 @Query(value="SELECT * FROM interested where property_id=:property_id", nativeQuery = true)
	 List<Interested> getInterestedDetailsByPropertyId(@Param("property_id") Integer propertyId);
	 @Query(value="select * from interested where  applied_date >= :fromDate and applied_date <= :toDate ", nativeQuery = true)
	 List<Interested> fetchreportdatabetween( @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
	 @Query(value="select * from interested where  applied_date >= :fromDate and applied_date <= :toDate and phone_number=:phone_number ", nativeQuery = true)
	 List<Interested> fetchReportDataBetweenbyPhoneNumber(@Param("phone_number") String phone_number, @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
	 
	 
}

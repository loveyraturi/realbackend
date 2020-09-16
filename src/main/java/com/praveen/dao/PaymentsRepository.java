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
import com.praveen.model.Payments;
@Repository
@Transactional
public interface PaymentsRepository extends JpaRepository<Payments, Integer> {
	 @Query(value="SELECT * FROM payments where txnid=:txnid", nativeQuery = true)
	 Payments findByTxnId(@Param("txnid") String txnid);
	 
}

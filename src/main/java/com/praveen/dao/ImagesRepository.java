package com.praveen.dao;

import java.util.List;

import javax.transaction.Transactional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.Images;
@Repository
@Transactional
public interface ImagesRepository extends JpaRepository<Images, Integer> {
	 @Query(value="SELECT * FROM images where property_id=:property_id", nativeQuery = true)
	 List<Images> getImageByPropertyId(@Param("property_id") Integer propertyId);
}

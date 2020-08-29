package com.praveen.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.praveen.model.Images;

public interface ImagesRepository extends JpaRepository<Images, Integer> {
	 @Query(value="SELECT * FROM images where property_id=:property_id", nativeQuery = true)
	 List<Images> getImageByPropertyId(@Param("property_id") Integer propertyId);
}

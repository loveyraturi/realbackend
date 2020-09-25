package com.praveen.dao;

import java.sql.Timestamp;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.Images;
import com.praveen.model.Interested;
import com.praveen.model.PropertiesDetails;
import com.praveen.model.Users;
@Repository
@Transactional
public interface PropertiesDetailsRepository extends JpaRepository<PropertiesDetails, Integer> {
//	@Query(value="SELECT id, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details HAVING distance < 30 ORDER BY distance LIMIT 0 , 20;", nativeQuery = true)
//	 List<PropertiesDetails> findPropertiesNearMe(@Param("latitude") String latitude,@Param("longitude") String longitude);
	@Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details and is_approved=1 and isavailable=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> findPropertiesNearMe(@Param("latitude") String latitude,@Param("longitude") String longitude);
	 
	 //PROPERTY BY TYPE
	 @Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details where property_type=:property_type and isavailable=1 and is_approved=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> getNearByPropertyByType(@Param("latitude") String latitude,@Param("longitude") String longitude,@Param("property_type") String property_type);
	 
	 //PROPERTY BY TYPE AND RANGE
	 @Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details WHERE price > :priceRange and property_type=:property_type and isavailable=1 and is_approved=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> getNearByPropertyByGreaterRangeAndType(@Param("latitude") String latitude,@Param("longitude") String longitude,@Param("priceRange") String priceRange,@Param("property_type") String property_type);
	 @Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details WHERE price < :priceRange and property_type=:property_type and isavailable=1 and is_approved=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> getNearByPropertyByLessRangeAndType(@Param("latitude") String latitude,@Param("longitude") String longitude,@Param("priceRange") String priceRange,@Param("property_type") String property_type);
	 @Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details WHERE price between :priceRange and :priceRange2 and property_type=:property_type and isavailable=1 and is_approved=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> mainPropertiesRangePriceAndType(@Param("latitude") String latitude,@Param("longitude") String longitude,@Param("priceRange") String priceRange,@Param("priceRange2") String priceRange2,@Param("property_type") String property_type);
	 
	 //PROPERTY BY ADDRESS AND RANGE
	 @Query(value="SELECT * from properties_details WHERE (price > :priceRange and (city LIKE :address or locality LIKE :address or address LIKE :address) and isavailable=1 and is_approved=1) LIMIT 0 , 20", nativeQuery = true)
	 List<PropertiesDetails> getPropertyByGreaterRangeAndType(@Param("priceRange") String priceRange,@Param("address") String address);
	 @Query(value="SELECT * from properties_details WHERE (price < :priceRange and (city LIKE :address or locality LIKE :address or address LIKE :address) and isavailable=1 and is_approved=1) LIMIT 0 , 20 ", nativeQuery = true)
	 List<PropertiesDetails> getPropertyByLessRangeAndType(@Param("priceRange") String priceRange,@Param("address") String address);
	 @Query(value="SELECT * FROM properties_details WHERE ((city LIKE :address or locality LIKE :address or address LIKE :address) and price between :priceRange and :priceRange2) and isavailable=1 and is_approved=1", nativeQuery = true)
	 List<PropertiesDetails> mainPropertiesRangePriceAndAddress(@Param("address") String address,@Param("priceRange") String priceRange,@Param("priceRange2") String priceRange2);
	
	 
	 @Query(value="SELECT * from properties_details where id=:id LIMIT 0 , 20", nativeQuery = true)
	 PropertiesDetails fetchPropertiesById(@Param("id") int id);

	 
	 @Query(value="SELECT * from properties_details where is_approved=0", nativeQuery = true)
	 List<PropertiesDetails> fetchUnapprovedProperties();
	 
	 @Query(value=":condition", nativeQuery = true)
	 List<PropertiesDetails> searchProperties(@Param("condition") String condition);
	 
	 @Query(value="SELECT city,locality FROM properties_details WHERE city LIKE %:address% or locality LIKE %:address% ;", nativeQuery = true)
	 List<Object[]> searchAddress(@Param("address") String address);
	
	 
	 @Query(value="SELECT * FROM properties_details WHERE ((city LIKE :address or locality LIKE :address) and price > :priceRange) and property_type=:property_type and isavailable=1 and is_approved=1", nativeQuery = true)
	 List<PropertiesDetails> mainPropertiesGreaterPrice(@Param("address") String address,@Param("priceRange") String priceRange,@Param("property_type") String property_type);
	 @Query(value="SELECT * FROM properties_details WHERE ((city LIKE :address or locality LIKE :address) and price < :priceRange) and property_type=:property_type and isavailable=1 and is_approved=1", nativeQuery = true)
	 List<PropertiesDetails> mainPropertiesLessPrice(@Param("address") String address,@Param("priceRange") String priceRange,@Param("property_type") String property_type);
	 @Query(value="SELECT * FROM properties_details WHERE ((city LIKE :address or locality LIKE :address) and price between :priceRange and :priceRange2) and property_type=:property_type and isavailable=1 and is_approved=1", nativeQuery = true)
	 List<PropertiesDetails> mainPropertiesRangePrice(@Param("address") String address,@Param("priceRange") String priceRange,@Param("priceRange2") String priceRange2,@Param("property_type") String property_type);
	 
	 //PROPERTYBYRANGE
	 
	 @Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details WHERE price > :priceRange and isavailable=1 and is_approved=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> mainPropertiesGreaterPriceOnly(@Param("latitude") String latitude,@Param("longitude") String longitude,@Param("priceRange") String priceRange);
	 @Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details WHERE price < :priceRange and isavailable=1 and is_approved=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> mainPropertiesLessPriceOnly(@Param("latitude") String latitude,@Param("longitude") String longitude,@Param("priceRange") String priceRange);
	 @Query(value="SELECT id,name,address,bedroom,washroom,garage,description,area,owner_name,isavailable,phone_number,front_image,latitude,longitude,date_created,date_modified,price,parking,locality, (3959 * acos (cos ( radians(:latitude) )* cos( radians( latitude ) )* cos( radians( longitude ) - radians(:longitude) ) + sin ( radians(:latitude) ) * sin( radians( latitude ) ) )) AS distance FROM properties_details WHERE price between :priceRange and :priceRange2 and isavailable=1 and is_approved=1 HAVING distance < 30 ORDER BY distance LIMIT 0 , 20", nativeQuery = true)
	 List<Object[]> mainPropertiesRangePriceOnly(@Param("latitude") String latitude,@Param("longitude") String longitude,@Param("priceRange") String priceRange,@Param("priceRange2") String priceRange2);
	
	 //BY ADDRESS
	 @Query(value="SELECT * FROM properties_details WHERE (city LIKE :address or locality LIKE :address or address LIKE :address) and isavailable=1 and is_approved=1", nativeQuery = true)
	 List<PropertiesDetails> getPropertyByAddress(@Param("address") String address);
	 //BYADDRESSANDTYPE
	 @Query(value="SELECT * FROM properties_details WHERE (city LIKE :address or locality LIKE :address or address LIKE :address) and property_type=:type and isavailable=1 and is_approved=1", nativeQuery = true)
	 List<PropertiesDetails> getPropertyByTypeAndAddress(@Param("type") String type,@Param("address") String address);
	 
	 @Query(value="select * from properties_details where  date_created >= :fromDate and date_created <= :toDate ", nativeQuery = true)
	 List<PropertiesDetails> fetchreportdatabetweenpropertyadded( @Param("fromDate") Timestamp fromDate,@Param("toDate") Timestamp toDate);
	 @Query(value="select * from properties_details where  owner_name=:ownerName", nativeQuery = true)
	 List<PropertiesDetails> findByOwnerName( @Param("ownerName") String ownerName);
	
	 //	@Query(value="select group_campaing_mapping.campaingname from group_campaing_mapping INNER JOIN users as table1 ON table1.usergroup = group_campaing_mapping.groupname where table1.username=:username", nativeQuery = true)
//	 List<String> findCampaingByUserName(@Param("username") String username);
//	@Query(value="select * from campaing  where name=:name ", nativeQuery = true)
//	 Campaing findCampaingByName(@Param("name") String name);
//	@Query(value="select * from campaing  where active='Y' ", nativeQuery = true)
//	List<Campaing> findActiveCampaing();	
	
	 @Query(value="select  properties_details.* from  properties_details INNER JOIN interested ON properties_details.id=interested.property_id where interested.username=:username", nativeQuery = true)
	 List<PropertiesDetails> fetchInterestedPropertyByUsername(@Param("username") String username);
}

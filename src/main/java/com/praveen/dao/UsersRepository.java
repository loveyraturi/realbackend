package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.Interested;
import com.praveen.model.Users;

import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface UsersRepository extends JpaRepository<Users, Integer> {
	
	@Query(value="SELECT * FROM users", nativeQuery = true)
	List<Users> findAllTenants();
	@Query(value="SELECT * FROM users where (email=:type or username=:type)", nativeQuery = true)
	Users searchUserByEmailOrUsername(@Param("type") String type);
	@Query(value="SELECT * FROM users where (email=:email and password=:password) or (phone_number=:email and password=:password) LIMIT 1", nativeQuery = true)
	Users validateUser(@Param("email") String email,@Param("password") String password);	
	
	@Query(value="SELECT * FROM users where username=:username", nativeQuery = true)
	Users findByUsername(@Param("username") String username);
	@Query(value="SELECT * FROM users where email=:email LIMIT 1", nativeQuery = true)
	Users findByEmail(@Param("email") String email);
	@Query(value="SELECT * FROM users where phone_number=:phoneNumber LIMIT 1", nativeQuery = true)
	Users findByPhoneNumber(@Param("phoneNumber") String phoneNumber);
	@Query(value="SELECT * FROM users where email=:email and uuid=:uuid", nativeQuery = true)
	Users findByEmailAndUuid(@Param("email") String email,@Param("uuid") String uuid);
	
	@Query(value="SELECT users.username,users.email,users.phone_number,interested.property_id,interested.status,interested.appointment,interested.emp_proof,interested.emp_type,interested.filename FROM users LEFT JOIN interested on users.username=interested.username where users.id=:id", nativeQuery = true)
	List<Object[]> findUserDetailsById(@Param("id") int id);
	
//	@Query(value="SELECT * FROM users where username=:username", nativeQuery = true)
//	List<Users> findByUsername(@Param("username") String username);	
//	
//	@Query(value="SELECT * FROM users where usergroup=:usergroup", nativeQuery = true)
//	List<Users> findUserByGroupName(@Param("usergroup") String usergroup);	
//	
//	@Query(value="SELECT distinct table1.campaingname FROM users INNER JOIN group_campaing_mapping as table1 ON users.usergroup = table1.groupname where users.username IN (:username) and table1.campaingname IN (:campaingname)", nativeQuery = true)
//	List<String> fetchCampaingOfUser(@Param("username") List<String> username,@Param("campaingname") List<String> campaingname);	
//	
//	@Query(value="SELECT users.* FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name=:campaingName ", nativeQuery = true)
//	List<Users> fetchActiveCampaingWithUsers(@Param("campaingName") String campaingName);
//	
//	@Query(value="SELECT users.* FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name=:campaingName and users.online=1", nativeQuery = true)
//	List<Users> fetchOnlineUsersByCampaingName(@Param("campaingName") String campaingName);
//	
//	@Query(value="SELECT users.username FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name=:campaingName ", nativeQuery = true)
//	List<String> fetchUserNameByCampaingName(@Param("campaingName") String campaingName);
//	
//	@Query(value="SELECT users.username FROM campaing LEFT JOIN group_campaing_mapping ON group_campaing_mapping.campaingname=campaing.name LEFT JOIN users ON group_campaing_mapping.groupname=users.usergroup where campaing.name IN (:campaingName) ", nativeQuery = true)
//	List<String> fetchUserNameByCampaingNames(@Param("campaingName") List<String> campaingName);
//	
	
//	@Modifying
//	@Query(value="DELETE FROM `versions` WHERE version=:version and filename=:filename", nativeQuery = true)
//	 void deleteByVersionAndFilename(@Param("filename") String filename,@Param("version") String version);	

}

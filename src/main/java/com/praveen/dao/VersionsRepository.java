package com.praveen.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.praveen.model.Versions;

import java.util.ArrayList;
import java.util.List;

import javax.transaction.Transactional;

@Repository
@Transactional
public interface VersionsRepository extends JpaRepository<Versions, Integer> {
	
	@Query(value="SELECT id,filename,enabled,version FROM versions WHERE filename=:filename and version=:version", nativeQuery = true)
	 List<Versions> findByFilenameAndVersion(@Param("filename")String filename,@Param("version") String version);
	
	@Query(value="SELECT distinct id,filename,enabled,version FROM versions WHERE filename=:filename", nativeQuery = true)
	 List<Versions> findAllByName(@Param("filename") String filename);
	
	@Query(value="SELECT max(version) FROM `versions` where filename=:filename", nativeQuery = true)
	 String findMaxVersion(@Param("filename") String filename);	
	
	
	@Modifying
	@Query(value="DELETE FROM `versions` WHERE version=:version and filename=:filename", nativeQuery = true)
	 void deleteByVersionAndFilename(@Param("filename") String filename,@Param("version") String version);	

}

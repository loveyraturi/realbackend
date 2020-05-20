package com.praveen.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mysql.fabric.xmlrpc.base.Array;
import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.UserGroupMappingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Campaing;
import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.Leads;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;


import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Service
public class UsersService {

	@Autowired
	private UsersRepository userRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	UserGroupMappingRepository userGroupMappingRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	LeadsRepository leadsRepository;
	@Autowired
	CampaingRepository campaingRepository;
	@Autowired
	CampaingLeadMappingRepository campaingLeadMappingRepository;
	

//	public Map<String, String> saveVersion(Map<String, String> request) {
//		Versions version = new Versions();
//		version.setFilename(request.get("filename"));
//		version.setVersion(request.get("version"));
//		version.setEnabled("1");
//		System.out.println(version.getFilename() + "###################");
//		Versions resp = repository.save(version);
//		Map<String, String> response = new HashMap<String, String>();
//		response.put("id", String.valueOf(resp.getId()));
//		response.put("status", "success");
//		return response;
//	}

	public Map<String, String> validateUser(Map<String,String> request) {
		Map<String, String> response = new HashMap<>();
		
				if(userRepository.validateUser(request.get("username"),request.get("password"))==null){
					response.put("status", "false");
				}else{
					Users user=userRepository.validateUser(request.get("username"),request.get("password"));
					if(user.getId()>0) {
						response.put("status", "true");
					}else {
						response.put("status", "false");
					}
				}
				
		return response;
	}
	
	public List<Leads> fetchLeadsByUserAndCampaing(Map<String,String> request) {
		List<Leads> response = new ArrayList<>();
		userGroupMappingRepository.findGroupByUsername(request.get("username")).forEach((groupMapping)->{
			List<Campaing> campaings = campaingRepository.findCampaingByGroupName(groupMapping.getGroupname(),request.get("campaing"));
//			int count=0;
			campaings.forEach((campaing)-> {
				List<Leads> leads =leadsRepository.findLeadsByCampaingId(campaing.getId());
				response.addAll(leads);
			});
		});
		for(int i=0;i<response.size();i++) {
			if(i==0) {
				System.out.println(response);
				Leads currentLead = response.get(i);
				System.out.println("############################");
				System.out.println(response.get(i).getId());
				System.out.println(request.get("username"));
				currentLead.setStatus("OCCUPIED");
				currentLead.setName(request.get("username"));
				leadsRepository.save(currentLead);
			}
		}
		return response;
	}
	
	public Map<String, String> createUser(Map<String,String> request) {
		Map<String, String> response = new HashMap<>();
		Users user=new Users();
		user.setFullName(request.get("fullname"));
		user.setLevel(Integer.parseInt(request.get("level")));
		user.setPassword(request.get("password"));
		user.setUsername(request.get("username"));
		userRepository.save(user);
    	response.put("status", "true");
		return response;
	}
	
	public Map<String, String> attachUserWithGroup(Map<String,String> request) {
		Map<String, String> response = new HashMap<>();
		Optional<Users> user = userRepository.findById(Integer.parseInt(request.get("user_id")));
		if (user.isPresent()) {
			Users userFound= user.get();
			Optional<UserGroup> group = userGroupRepository.findById(Integer.parseInt(request.get("group_id")));
			if (group.isPresent()) {
				UserGroup userGroup=group.get();
				UserGroupMapping userGroupMapping = new UserGroupMapping();
		     	userGroupMapping.setUsername(userFound.getFullName());
		     	userGroupMapping.setGroupname(userGroup.getName());
		     	userGroupMappingRepository.save(userGroupMapping);
			}
		}
		
    	response.put("status", "true");
		return response;
	}
	
	public Map<String, String> createUserWithGroup(Map<String,String> request) {
		Map<String, String> response = new HashMap<>();
		Users user=new Users();
		user.setFullName(request.get("fullname"));
		user.setLevel(Integer.parseInt(request.get("level")));
		user.setPassword(request.get("password"));
		user.setUsername(request.get("username"));
		UserGroup userGroup = new UserGroup();
		userGroup.setActive(request.get("group_status"));
		userGroup.setName(request.get("groupname"));
		userGroupRepository.save(userGroup);
		userRepository.save(user);
     	UserGroupMapping userGroupMapping = new UserGroupMapping();
     	userGroupMapping.setUsername(request.get("fullName"));
     	userGroupMapping.setGroupname(request.get("name"));
     	userGroupMappingRepository.save(userGroupMapping);
    	response.put("status", "true");
		return response;
	}
	
	public void createUser(Users user){
		userRepository.save(user);
	}
	public void loadCSV(Users users,Campaing campaing,UserGroup usergroup,UserGroupMapping ugm,GroupCampaingMapping gcm) {
		userRepository.save(users);
		campaingRepository.save(campaing);
		userGroupRepository.save(usergroup);
		userGroupMappingRepository.save(ugm);
		groupCampaingMappingRepository.save(gcm);
	}
	public void loadCSVLead(Leads leads){
		Leads resp= leadsRepository.save(leads);
		CampaingLeadMapping clm = new CampaingLeadMapping();
		clm.setCampaingid(campaingRepository.findCampaingByName("HATHWAY").getId());
		clm.setLeadid(resp.getId());
		campaingLeadMappingRepository.save(clm);
		
		
	}
//
//	public void deleteByVersionAndFilename(String version, String filename) {
//		repository.deleteByVersionAndFilename(filename, version);
//	}
//
//	public ArrayList<Versions> fetchAllVersions() {
//		return (ArrayList<Versions>) repository.findAll();
//	}
//
//	public ArrayList<Versions> fetchByName(String name) {
//
//		return (ArrayList<Versions>) repository.findAllByName(name);
//	}
//
//	public List<Versions> fetchByFilenameAndVersions(String filename, String version) {
//		return repository.findByFilenameAndVersion(filename, version);
//	}
//
//	public static boolean deleteDirectory(File dir) {
//		if (dir.isDirectory()) {
//			File[] children = dir.listFiles();
//			for (int i = 0; i < children.length; i++) {
//				boolean success = deleteDirectory(children[i]);
//				if (!success) {
//					return false;
//				}
//			}
//		}
//		System.out.println("removing file or directory : " + dir.getName());
//		return dir.delete();
//	}
}

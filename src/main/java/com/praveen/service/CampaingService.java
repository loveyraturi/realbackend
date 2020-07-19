package com.praveen.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.StatusRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.model.Campaing;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.Leads;
import com.praveen.model.Status;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

@Service
public class CampaingService {
	@Autowired
	CampaingRepository campaingRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	StatusRepository statusRepository;
	@Autowired
	LeadsRepository leadsRepository;

	public List<Campaing> fetchCampaingByUser(List<Integer> ids) {
		return campaingRepository.findAllById(ids);
	}
	
	
	public Map<String,String> fetchStatuByCampaingName(String CampaingName) {
		Map<String,String> response = new HashMap<>();
		Status status= statusRepository.findStatusCampaingName(CampaingName);
		if(status!=null) {
			response.put("status",status.getStatus());
		}else {
			response.put("status","");
		}
		 
		return response;
	}
	public JsonNode fetchCrm(String leadid) {
		JsonNode response = null;
		ObjectMapper mapper = new ObjectMapper();
		Leads leads= leadsRepository.findById(Integer.parseInt(leadid))!=null?leadsRepository.findById(Integer.parseInt(leadid)).get():new Leads();
		if(leads!=null) {
//			JsonParser parser = new JsonParser(); 
			
			String fields=leads.getCrm()!=null?leads.getCrm():"";
			System.out.println(fields.isEmpty());
			if(!fields.isEmpty()) {
				System.out.println("inside");
			 try {
				response = mapper.readTree(fields);
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			}else {
				try {
					response = mapper.readTree("{}");
				} catch (JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (JsonProcessingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

		}else {
			try {
				response = mapper.readTree("{}");
			} catch (JsonMappingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JsonProcessingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		 
		return response;
	}
	

	public List<Campaing> fetchCampaing() {
		return campaingRepository.findAll();
	}

	public void createCampaing(Map<String, Object> request) {
		Gson gson = new Gson();
		String additionalFields=gson.toJson(request.get("additionalFields"));
		Campaing campaing = new Campaing();
		campaing.setActive(String.valueOf(request.get("active")));
		campaing.setName(String.valueOf(request.get("name")));
		campaing.setDialPrefix(String.valueOf(request.get("dial_prefix")));
		campaing.setAssignmentType(String.valueOf(request.get("assignmentType")));
		campaing.setLocalCallTime(String.valueOf(request.get("local_call_time")));
		campaing.setDialTimeout(String.valueOf(request.get("dial_timeout")));
		campaing.setManualDialPrefix(String.valueOf(request.get("manual_dial_prefix")));
		campaing.setAdditionalField(additionalFields);
		campaingRepository.save(campaing);
		if(request.containsKey("status")) {
			Status status= new Status();
			status.setCampaingName(campaing.getName());
			status.setStatus(String.valueOf(request.get("status")));
			statusRepository.save(status);
		}
	}
	
	public Map<String, String> fetchCampaingByUserName(String username){
		Map<String,String> campaingMap= new HashMap<String,String>();
		 campaingMap.put("campaing", String.join(",", campaingRepository.findCampaingByUserName(username)));
		 return campaingMap;
	}

	public Map<String, String> updateCampaing(Map<String, Object> request) {
		
		Campaing campaing = campaingRepository.findById(Integer.parseInt(String.valueOf(request.get("id")))).get();
		Gson gson = new Gson();
		String additionalFields=gson.toJson(request.get("additionalFields"));
		campaing.setActive(String.valueOf(request.get("active")));
		campaing.setName(String.valueOf(request.get("name")));
		campaing.setDialPrefix(String.valueOf(request.get("dial_prefix")));
		campaing.setLocalCallTime(String.valueOf(request.get("local_call_time")));
		campaing.setAssignmentType(String.valueOf(request.get("assignmentType")));
		campaing.setDialTimeout(String.valueOf(request.get("dial_timeout")));
		campaing.setManualDialPrefix(String.valueOf(request.get("manual_dial_prefix")));
		campaing.setAdditionalField(additionalFields);
		campaingRepository.save(campaing);
		Map<String, String> response = new HashMap<String, String>();
		response.put("status", "true");
		return response;

	}

	public void campaingGroupMapping(Map<String, String> request) {
		GroupCampaingMapping groupCampaingMapping = new GroupCampaingMapping();
		groupCampaingMapping.setCampaingname(request.get("campaingname"));
		groupCampaingMapping.setGroupname(request.get("groupname"));
		groupCampaingMappingRepository.save(groupCampaingMapping);
	}

	public void updateCampaingGroupMapping(Map<String, String> request) {
		System.out.println(request);
		GroupCampaingMapping groupCampaingMapping = groupCampaingMappingRepository
				.findGroupByCampaingName(request.get("campaingname"));
		groupCampaingMapping.setCampaingname(request.get("campaingname"));
		groupCampaingMapping.setGroupname(request.get("groupname"));
		groupCampaingMappingRepository.save(groupCampaingMapping);
	}

	public void attachUserGroupToCampaing(Map<String, String> request) {
		Optional<UserGroup> userGroup = userGroupRepository.findById(Integer.parseInt(request.get("group_id")));
		if (userGroup.isPresent()) {
			UserGroup userGroupFound = userGroup.get();
			Optional<Campaing> campaing = campaingRepository.findById(Integer.parseInt(request.get("campaing_id")));
			if (campaing.isPresent()) {
				Campaing campaingFound = campaing.get();
				GroupCampaingMapping groupCampaingMapping = new GroupCampaingMapping();
				groupCampaingMapping.setCampaingname(campaingFound.getName());
				groupCampaingMapping.setGroupname(userGroupFound.getName());
				System.out.println(campaingFound.getName());
				System.out.println(userGroupFound.getName());
				groupCampaingMappingRepository.save(groupCampaingMapping);
			}
		}
	}
}

package com.praveen.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.model.Campaing;
import com.praveen.model.GroupCampaingMapping;
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
	
	
	public List<Campaing> fetchCampaingByUser(List<Integer> ids){
		return campaingRepository.findAllById(ids);
	}
	
	public List<Campaing> fetchCampaing(){
		return campaingRepository.findAll();
	}
	

	public void createCampaing(Map<String, String> request) {
		Campaing campaing = new Campaing();
		campaing.setActive(request.get("active"));
		campaing.setName(request.get("name"));
		campaing.setDialPrefix(request.get("dial_prefix"));
		campaing.setLocalCallTime(request.get("local_call_time"));
		campaing.setDialTimeout(request.get("dial_timeout"));
		campaing.setManualDialPrefix(request.get("manual_dial_prefix"));
		campaingRepository.save(campaing);
	}
	
	public void attachUserGroupToCampaing(Map<String, String> request) {
		Optional<UserGroup> userGroup = userGroupRepository.findById(Integer.parseInt(request.get("group_id")));
		if (userGroup.isPresent()) {
			UserGroup userGroupFound= userGroup.get();
			Optional<Campaing> campaing = campaingRepository.findById(Integer.parseInt(request.get("campaing_id")));
			if (campaing.isPresent()) {
				Campaing campaingFound = campaing.get();
				GroupCampaingMapping groupCampaingMapping= new GroupCampaingMapping();
				groupCampaingMapping.setCampaingname(campaingFound.getName());
				groupCampaingMapping.setGroupname(userGroupFound.getName());
				System.out.println(campaingFound.getName());
				System.out.println(userGroupFound.getName());
				groupCampaingMappingRepository.save(groupCampaingMapping);				
			}
		}
	}
}

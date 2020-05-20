package com.praveen.service;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dao.UserGroupMappingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;

@Service
public class UserGroupService {

	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	UsersRepository userRepository;
	@Autowired
	UserGroupMappingRepository userGroupMappingRepository;

	public Optional<UserGroup> fetchUserGroupById(Integer id) {
		return userGroupRepository.findById(id);
	}

	public void createUserGroup(Map<String, String> request) {
		UserGroup userGroup = new UserGroup();
		userGroup.setActive(request.get("active"));
		userGroup.setName(request.get("name"));
		userGroupRepository.save(userGroup);
	}

	public void attachUserToGroup(Map<String, String> request) {
		Optional<Users> user = userRepository.findById(Integer.parseInt(request.get("user_id")));
		if (user.isPresent()) {
			Users userFound=user.get();
			Optional<UserGroup> group = userGroupRepository.findById(Integer.parseInt(request.get("group_id")));
			if (group.isPresent()) {
				UserGroup userGroup=group.get();
				UserGroupMapping userGroupMapping = new UserGroupMapping();
		     	userGroupMapping.setUsername(userFound.getFullName());
		     	userGroupMapping.setGroupname(userGroup.getName());
		     	userGroupMappingRepository.save(userGroupMapping);
			}
		}

	}
}

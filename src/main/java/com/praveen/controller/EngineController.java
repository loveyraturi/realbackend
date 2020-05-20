package com.praveen.controller;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import com.praveen.dao.LeadsRepository;
import com.praveen.model.Campaing;
import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.Leads;
import com.praveen.model.UserGroup;
import com.praveen.model.UserGroupMapping;
import com.praveen.model.Users;
import com.praveen.service.CampaingService;
import com.praveen.service.LeadsService;
import com.praveen.service.UserGroupService;
import com.praveen.service.UsersService;

@RestController
@RequestMapping("/goautodial")
public class EngineController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private UsersService usersService;

	@Autowired
	UserGroupService userGroupService;
	@Autowired
	CampaingService campaingService;
	@Autowired
	LeadsService leadsService;

	@GetMapping("/")
	public String getEmployees() {
		return "hihihi";
	}

	@PostMapping(path = "/validateuser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> validateUser(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.validateUser(resp);
	}
	
	@PostMapping(path = "/statusCall", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> statusCall(@RequestBody(required = true) Map<String, String> resp) {
		
		return usersService.validateUser(resp);
	}
	@PostMapping(path = "/loadCsv", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> loadCsv(@RequestParam("file") MultipartFile file) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(file.getInputStream());
		
//			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
	       CSVParser parser = new CSVParser( reader, CSVFormat.DEFAULT );
	        List<CSVRecord> list = parser.getRecords();
	        UserGroup userGroup=new UserGroup();
    		userGroup.setActive("Y");
    		userGroup.setName(list.get(2).get(4));
    		Campaing campaing = new Campaing();
    		campaing.setActive("Y");
    		campaing.setDialPrefix("555");
    		campaing.setDialTimeout("100");
    		campaing.setLocalCallTime("9AM - 10PM");
    		campaing.setManualDialPrefix("555");
    		campaing.setName(list.get(2).get(3));
	        for( int j=1;j<list.size();j++) {
//	        	for(int i=0;i<=list.get(j).size();i++) {
	        		Users user= new Users();
	        		user.setFullName(list.get(j).get(1));
	        		user.setUsername(list.get(j).get(1));
	        		user.setPassword(list.get(j).get(2));
	        		user.setLevel(Integer.parseInt(list.get(j).get(5)));
	        		
	        		UserGroupMapping userGroupMapping =new UserGroupMapping();
	        		userGroupMapping.setGroupname(userGroup.getName());
	        		userGroupMapping.setUsername(user.getUsername());
	        		GroupCampaingMapping gcm = new GroupCampaingMapping();
	        		gcm.setCampaingname(campaing.getName());
	        		gcm.setGroupname(userGroup.getName());
	        		usersService.loadCSV(user,campaing,userGroup,userGroupMapping,gcm);
//	        	}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> response = new HashMap<>();
		return response;
	}
	
	@PostMapping(path = "/loadCsvLead", consumes = "multipart/form-data", produces = "application/json")
	public Map<String, String> loadCsvLead(@RequestParam("file") MultipartFile file) {
		Reader reader = null;
		try {
			reader = new InputStreamReader(file.getInputStream());
		
//			CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
	       CSVParser parser = new CSVParser( reader, CSVFormat.DEFAULT );
	        List<CSVRecord> list = parser.getRecords();
	        for( int j=1;j<list.size();j++) {
Leads leads = new Leads();
leads.setFirstName(list.get(j).get(2));
leads.setCity(list.get(j).get(5));
leads.setState(list.get(j).get(6));
leads.setStatus("ACTIVE");
leads.setLastName(list.get(j).get(2));
leads.setPhoneNumber(list.get(j).get(0));
leads.setCity(list.get(j).get(5));
leads.setPostalCode(list.get(j).get(4));
leads.setAddress1(list.get(j).get(3));
leads.setEmail(list.get(j).get(7));
	        		usersService.loadCSVLead(leads);
//	        	}
	        }
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/createUserGroup", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> createUserGroup(@RequestBody(required = true) Map<String, String> request) {
		userGroupService.createUserGroup(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/createCampaing", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> createCampaing(@RequestBody(required = true) Map<String, String> request) {
		campaingService.createCampaing(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/createLead", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> createLead(@RequestBody(required = true) Map<String, String> request) {
		leadsService.createLead(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/attachLeadToCampaing", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> attachLeadToCampaing(@RequestBody(required = true) Map<String, String> request) {
		leadsService.attachLeadToCampaing(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/attachUserGroupToCampaing", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> attachUserGroupToCampaing(@RequestBody(required = true) Map<String, String> request) {
		campaingService.attachUserGroupToCampaing(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/createUserWithGroup", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> createUserWithGroup(@RequestBody(required = true) Map<String, String> request) {
		usersService.createUserWithGroup(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	@PostMapping(path = "/createUser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> createUser(@RequestBody(required = true) Map<String, String> request) {
		usersService.createUser(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}

	
	@PostMapping(path = "/feedback", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> feedback(@RequestBody(required = true) Map<String, String> request) {
		leadsService.feedback(request);
		Map<String, String> response = new HashMap<>();
		return response;
	}
	
	@GetMapping(path = "/findGroupById/{id}")
	@ResponseBody
	public Optional<UserGroup> findGroupById(@PathVariable("id") int id) {
		return userGroupService.fetchUserGroupById(id);
	}

	@GetMapping(path = "/fetchCampaing")
	@ResponseBody
	public String fetchCampaing() {
		List<String> campaingName = new ArrayList<>();
		campaingService.fetchCampaing().forEach((campaing) -> {
			campaingName.add(campaing.getName());
		});
		return String.join(",", campaingName);
	}
	
	
	

	@GetMapping(path = "/fetchLeadsByUserAndCampaing/{username}/{campaing}")
	@ResponseBody
	public List<Leads> fetchLeadsByUserAndCampaing(@PathVariable("username") String username,
			@PathVariable("campaing") String campaing) {
		Map<String, String> request = new HashMap<>();
		request.put("username", username);
		request.put("campaing", campaing);
		return usersService.fetchLeadsByUserAndCampaing(request);
	}

}

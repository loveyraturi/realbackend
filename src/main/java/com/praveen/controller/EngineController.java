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
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.apache.commons.collections4.MultiMap;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.praveen.model.PropertiesDetails;

import org.apache.commons.collections4.MultiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
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
import com.praveen.dao.UsersRepository;
import com.praveen.service.PropertiesDetailsService;
import com.praveen.service.UsersService;

@RestController
@RequestMapping("/realestate")
public class EngineController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	UsersService usersService;
	@Autowired
	PropertiesDetailsService propertiesDetailsService;
	@Value("${project.location}")
	String projectLocation;
	
	@Autowired
	private UsersRepository userRepository;
	
	@GetMapping("/")
	public String getEmployees() {
		return "hihihi";
	}
	@CrossOrigin
	@GetMapping("/searchAddress/{address}")
	public List<String> searchAddress(@PathVariable("address") String address) {
		return propertiesDetailsService.searchAddress(address);
	}
	@CrossOrigin
	@GetMapping("/fetchPropertiesById/{id}")
	public Map<String, Object>  fetchPropertiesById(@PathVariable("id") int id) {
		return propertiesDetailsService.fetchPropertiesById(id);
	}
	
	@CrossOrigin
	@PostMapping(path = "/searchProperties", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<PropertiesDetails> searchProperties(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.searchProperties(resp);
	}
	
	@CrossOrigin
	@PostMapping(path = "/fetchreportdatabetween", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ByteArrayResource fetchreportdatabetween(@RequestBody(required = true) Map<String, Object> resp) {
		return usersService.fetchreportdatabetween(resp,projectLocation);
	}
	@CrossOrigin
	@PostMapping(path = "/fetchreportdatabetweenpropertyadded", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public ByteArrayResource fetchreportdatabetweenpropertyadded(@RequestBody(required = true) Map<String, Object> resp) {
		return propertiesDetailsService.fetchreportdatabetweenpropertyadded(resp,projectLocation);
	}
	
	@CrossOrigin
	@PostMapping(path = "/mainProperties", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Map<String,Object>> mainProperties(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.mainProperties(resp);
	}
	@CrossOrigin
	@PostMapping(path = "/validateuser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> validateUser(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.validateUser(resp);
	}
	@CrossOrigin
	@PostMapping(path = "/addProperties", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> addProperties(@RequestBody(required = true) Map<String, Object> resp) {
		Map<String,String> response= new HashMap<>();
		propertiesDetailsService.addProperties(resp,projectLocation);
		 response.put("status", "true");
		 return response;
	}
	@CrossOrigin
	@PostMapping(path = "/findPropertiesNearMe", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> findPropertiesNearMe(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.findPropertiesNearMe(resp);
		 
	}
	@CrossOrigin
	@PostMapping(path = "/registerUser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> registerUser(@RequestBody(required = true) Map<String, String> resp) {
		System.out.println(resp);
		Map<String,String> response= new HashMap<>();
		usersService.registerUser(resp);
		 response.put("status", "true");
		 return response;
	}
	@CrossOrigin
	@PostMapping(path = "/interested", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> interested(@RequestBody(required = true) Map<String, String> resp) {
		System.out.println(resp);
		return usersService.interested(resp);

	}

}

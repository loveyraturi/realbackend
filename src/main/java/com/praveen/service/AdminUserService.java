package com.praveen.service;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.data.repository.CrudRepository;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.praveen.dao.AdminUserRepository;
import com.praveen.dao.InterestedRepository;
import com.praveen.dao.PropertiesDetailsRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.AdminUser;
import com.praveen.model.Interested;
import com.praveen.model.Users;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.Date;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import javax.annotation.PostConstruct;

@Service
public class AdminUserService {

	@Autowired
	private AdminUserRepository adminUserRepository;
	
	@PostConstruct
	  void postConstruct(){
	    AdminUser au= new AdminUser();
	    au.setId(1);
	    au.setUsername("admin");
	    au.setPassword("1234");
	    au.setLevel(9);
	    adminUserRepository.save(au);
	  }

		public Map<String, String> validateUser(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();

		if (adminUserRepository.validateUser(request.get("username"), request.get("password")) == null) {
			response.put("status", "false");
			response.put("message", "Invalid Username or password");
		} else {
			AdminUser user = adminUserRepository.validateUser(request.get("username"), request.get("password"));
			if (user.getId() > 0) {
				
				response.put("status", "true");
				response.put("message", "Successfully logged in");
			} else {
				response.put("status", "false");
				response.put("message", "Invalid Username or password");
			}
		}

		return response;
	}

}

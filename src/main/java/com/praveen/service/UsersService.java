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
import com.mysql.fabric.xmlrpc.base.Array;
import com.praveen.dao.InterestedRepository;
import com.praveen.dao.PropertiesDetailsRepository;
import com.praveen.dao.UsersRepository;
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

@Service
public class UsersService {

	@Autowired
	private UsersRepository userRepository;
	@Autowired
	PropertiesDetailsRepository propertiesDetailsRepository;
	@Autowired
	InterestedRepository interestedRepository;
	public Users searchUserByEmailOrUsername(String type){
		System.out.println(type);
		return userRepository.searchUserByEmailOrUsername(type);
	}
	public Map<String, String> validateUserName(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		if (userRepository.findByUsername(request.get("username")) != null) {
			response.put("status", "true");
		} else {
			response.put("status", "false");
		}
		return response;
	}
	public Map<String, String> validateEmail(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		if (userRepository.findByEmail(request.get("email")) != null) {
			response.put("status", "true");
		} else {
			response.put("status", "false");
		}
		return response;
	}
	
	public Map<String, String> resetPassword(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		Users user=userRepository.findByEmailAndUuid(request.get("email"),request.get("uuid"));
		if(user!=null) {
			user.setPassword(request.get("password"));
			userRepository.save(user);
			response.put("status", "true");
			response.put("message", "Successfully updated Password");
		}else {
			response.put("status", "false");
			response.put("message", "Error Authenticating user");
		}
		
		return response;
	}
	public Map<String, String> validateUser(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();

		if (userRepository.validateUser(request.get("email"), request.get("password")) == null) {
			response.put("status", "false");
			response.put("message", "Invalid email or password");
		} else {
			Users user = userRepository.validateUser(request.get("email"), request.get("password"));
			if (user.getId() > 0) {
				// if (user.getOnline() == null) {
				user.setOnline("1");
				response.put("status", "true");
				response.put("name", user.getFullName());
				response.put("message", "Successfully logged in");
				userRepository.save(user);
				// } else if ("0".equals(user.getOnline()) || user.getOnline().isEmpty()) {
				// user.setOnline("1");
				// response.put("status", "true");
				// response.put("type", user.getType());
				// response.put("message", "Successfully logged in");
				// userRepository.save(user);
				//
				// } else {
				// response.put("status", "false");
				// response.put("type", "");
				// response.put("message", "User already logged in");
				// }

			} else {
				response.put("status", "false");
				response.put("message", "Invalid Username or password");
			}
		}

		return response;
	}

	public Map<String, String> registerUser(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();
		if (request.get("password") == null) {
			response.put("status", "false");
			response.put("message", "Please Enter Password");
			return response;

		} else if (request.get("phoneNumber") == null) {
			response.put("status", "false");
			response.put("message", "Please Enter Phonenumber");
			return response;

		} else if (request.get("email") == null) {
			response.put("status", "false");
			response.put("message", "Please Enter Email");
			return response;

		} else {
			if(userRepository.findByEmail(request.get("email"))==null && userRepository.findByPhoneNumber(request.get("phoneNumber"))==null) {
				response.put("status", "true");
				response.put("message", "Successfully Registered");
				Users users = new Users();
				users.setDeviceId(request.get("deviceId"));
				users.setFullName(request.get("name"));
				users.setPassword(request.get("password"));
				users.setStatus(request.get("status"));
				users.setPhoneNumber(request.get("phoneNumber"));
				users.setEmail(request.get("email"));
				userRepository.save(users);
			}else {
				response.put("status", "false");
				response.put("message", "Email/Phone number already in use");
				return response;
			}
			
		}
		return response;

	}

	public Map<String, String> interested(Map<String, String> request) {
		Date appliedDate = new Timestamp((new Date()).getTime());
		String email = request.get("email");
		System.out.println(appliedDate);
		System.out.println(request.get("property_id"));
		Interested alreadyAvailable = interestedRepository.findByEmailAndPropety(email,
				request.get("property_id"));
		// System.out.println(alreadyAvailable.getId());
		Users user = userRepository.findByEmail(email);
		Map<String, String> response = new HashMap<>();
		// Interested interestedResponse=null;
		if (alreadyAvailable == null) {
			Interested interested = new Interested();
			interested.setFullName(user.getFullName());
			interested.setEmail(user.getEmail());
			interested.setAppliedDate(appliedDate);
			interested.setEmail(user.getEmail());
			interested.setPhoneNumber(user.getPhoneNumber());
			interested.setPropertyId(request.get("property_id"));
			interested.setStatus(request.get("status"));
			interestedRepository.save(interested);
			response.put("status", "true");
			response.put("message", "Thanks for showing your Interest");
		} else {
			response.put("status", "true");
			response.put("message",
					"Your application is already registerd with us your application id is " + alreadyAvailable.getId());
		}

		return response;
	}

	public ByteArrayResource fetchreportdatabetween(Map<String, Object> request, String reportingLocation) {
		String phoneNumber = (String) request.get("phone_number");
		String dateTo = String.valueOf(request.get("dateto"));
		String dateFrom = String.valueOf(request.get("datefrom"));
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
			Date dateToDate = dateFormat.parse(dateTo);
			Date dateFromDate = dateFormat.parse(dateFrom);
			dateToTimestamp = new java.sql.Timestamp(dateToDate.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFromDate.getTime());
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<Interested> resultLeads = new ArrayList<>();
		if (phoneNumber.equals("")) {
			resultLeads.addAll(interestedRepository.fetchreportdatabetween(dateFromTimestamp, dateToTimestamp));
		} else {
			resultLeads.addAll(interestedRepository.fetchReportDataBetweenbyPhoneNumber(phoneNumber, dateFromTimestamp,
					dateToTimestamp));
		}
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("InterestedReport");
			int rownum = 0;
			int cellnumHeader = 0;
			Row rowHeader = sheet.createRow(rownum++);
			Cell header1 = rowHeader.createCell(cellnumHeader++);
			header1.setCellValue("fullName");
			Cell header2 = rowHeader.createCell(cellnumHeader++);
			header2.setCellValue("email");
			Cell header3 = rowHeader.createCell(cellnumHeader++);
			header3.setCellValue("propertyId");
			Cell header4 = rowHeader.createCell(cellnumHeader++);
			header4.setCellValue("phoneNumber");
			Cell header5 = rowHeader.createCell(cellnumHeader++);
			header5.setCellValue("Email");
			Cell header6 = rowHeader.createCell(cellnumHeader++);
			header6.setCellValue("Status");
			Cell header7 = rowHeader.createCell(cellnumHeader++);
			header7.setCellValue("appliedDate");
			Font hlink_font = workbook.createFont();
			hlink_font.setUnderline(Font.U_SINGLE);
			hlink_font.setColor(IndexedColors.BLUE.getIndex());
			CellStyle hlink_style = workbook.createCellStyle();
			CreationHelper createHelper = workbook.getCreationHelper();
			for (Interested lead : resultLeads) {
				System.out.println(lead.getFullName());
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue(lead.getFullName());
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue(lead.getEmail());
				Cell cell3 = row.createCell(cellnum++);
				cell3.setCellValue(lead.getPropertyId());
				Cell cell4 = row.createCell(cellnum++);
				cell4.setCellValue(lead.getPhoneNumber());
				Cell cell5 = row.createCell(cellnum++);
				cell5.setCellValue(lead.getEmail());
				Cell cell6 = row.createCell(cellnum++);
				cell6.setCellValue(lead.getStatus());
				DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd hh:mm:ss");
				String strDate = dateFormat.format(lead.getAppliedDate());
				System.out.println("Converted Date: " + strDate);
				Cell cell7 = row.createCell(cellnum++);
				cell7.setCellValue(strDate);
			}
			File excelFile = new File(reportingLocation + "InterestedReport.xlsx");
			OutputStream fos = new FileOutputStream(excelFile);
			workbook.write(fos);
			fos.close();
			System.out.println("InterestedReport.xlsx written successfully on disk.");
			FileInputStream fis = new FileInputStream(excelFile);
			byte[] buf = new byte[1024];
			try {
				for (int readNum; (readNum = fis.read(buf)) != -1;) {
					stream.write(buf, 0, readNum); // no doubt here is 0
					System.out.println("read " + readNum + " bytes,");
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			byte[] bytes = stream.toByteArray();

			HttpHeaders header = new HttpHeaders();
			header.setContentType(new MediaType("application", "force-download"));
			header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=ProductTemplate.xlsx");
			return new ByteArrayResource(bytes);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}

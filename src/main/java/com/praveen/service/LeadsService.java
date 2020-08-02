package com.praveen.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.apache.poi.ss.usermodel.Row;
import org.apache.commons.collections4.MultiMap;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.map.MultiValueMap;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.tomcat.util.http.fileupload.ByteArrayOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.mysql.fabric.xmlrpc.base.Array;
import com.praveen.dao.AttendanceRepository;
import com.praveen.dao.CallLogsRepository;
import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadVersionsRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.RecordingRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Attendance;
import com.praveen.model.CallLogs;
import com.praveen.model.Campaing;
import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.GroupCampaingMapping;
import com.praveen.model.LeadVersions;
import com.praveen.model.Leads;
import com.praveen.model.UserGroup;
import com.praveen.model.Users;

@Service
public class LeadsService {
	@Autowired
	LeadsRepository leadRepository;
	@Autowired
	CampaingRepository campaingRepository;
	@Autowired
	CampaingLeadMappingRepository campaingLeadMappingRepository;
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	UserGroupRepository userGroupRepository;
	@Autowired
	GroupCampaingMappingRepository groupCampaingMappingRepository;
	@Autowired
	LeadsRepository leadsRepository;
	@Autowired
	LeadVersionsRepository leadVersionsRepository;
	@Autowired
	AttendanceRepository attendanceRepository;
	@Autowired
	RecordingRepository recordingRepository;
	@Autowired
	CallLogsRepository callLogsRepository;

	public List<Leads> fetchAllLeads() {
		return leadRepository.findAll();
	}

	public List<String> findLeadByCampaingName(String campaingName) {
		return leadRepository.findLeadByCampaingName(campaingName);
	}

	public void loadCsvLeadData(MultipartFile file, String campaing, String duplicateAction, String duplicateCheck,
			String duplicateField, String filename) {
		Reader reader = null;
		try {
			System.out.println(
					campaing + "##############duplicateAction= " + duplicateAction + "##########duplicateCheck= "
							+ duplicateCheck + "############duplicateField" + duplicateField + "####" + filename);
			reader = new InputStreamReader(file.getInputStream());
			Map<String, String> uniqueNumber = new HashMap<>();

			if (campaingRepository.findCampaingByName(campaing) == null) {
				System.out.println("campaing not found");
				Campaing campaingNew = new Campaing();
				campaingNew.setActive("Y");
				campaingNew.setDialPrefix("NA");
				campaingNew.setName(campaing);
				campaingRepository.save(campaingNew);
				GroupCampaingMapping gcm = new GroupCampaingMapping();
				gcm.setCampaingname(campaing);
				if (userGroupRepository.findGroupByName(campaing) == null) {
					System.out.println("group not found");
					UserGroup newUserGroup = new UserGroup();
					newUserGroup.setActive("Y");
					newUserGroup.setName(campaing);
					userGroupRepository.save(newUserGroup);
					gcm.setGroupname(campaing);
				} else {
					gcm.setGroupname(campaing);
				}
				groupCampaingMappingRepository.save(gcm);
			}
			CampaingLeadMapping clm = new CampaingLeadMapping();

			clm.setCampaingName(campaing);
			clm.setLeadVersionName(filename);
			campaingLeadMappingRepository.save(clm);
			// CSVReader csvReader = new CSVReaderBuilder(reader).withSkipLines(1).build();
			CSVParser parser = new CSVParser(reader, CSVFormat.DEFAULT);
			List<CSVRecord> list = parser.getRecords();
			List<Leads> leadsArray = new ArrayList<>();
			List<String> leadsFound = new ArrayList<>();
			if ("Campaing".equals(duplicateCheck)) {
				leadsFound = leadsRepository.findLeadByCampaingName(campaing);
			}
			CSVRecord headers = list.get(0);
			for (int j = 1; j < list.size(); j++) {
				if ("List".equals(duplicateCheck)) {
					if ("phone_number".equals(duplicateField)) {
						if (uniqueNumber.get(list.get(j).get(0)) != null) {
							continue;
						} else {
							uniqueNumber.put(list.get(j).get(0), list.get(j).get(0));
						}
					}
				} else if ("Campaing".equals(duplicateCheck)) {
					if (leadsFound.contains(list.get(j).get(0))) {
						continue;
					}

				} else if ("Global".equals(duplicateCheck)) {
					if ("phone_number".equals(duplicateField)) {
						List<Leads> lead = leadsRepository.findLeadByNumber(list.get(j).get(0));
						if (lead.size() > 0) {
							continue;
						}
					}
				}
				List<Map<String, String>> crmList = new ArrayList<>();

				for (int i = 6; i < list.get(j).size(); i++) {
					Map<String, String> crm = new HashMap<>();
					crm.put("label", headers.get(i).replaceAll(" ", "_"));
					crm.put("field", list.get(j).get(i));
					crmList.add(crm);
				}

				Leads leads = new Leads();
				leads.setPhoneNumber(list.get(j).get(0));
				leads.setFirstName(list.get(j).get(1));
				leads.setState(list.get(j).get(2));
				leads.setCity(list.get(j).get(3));
				leads.setEmail(list.get(j).get(4));
				Gson gson = new Gson();
				String additionalFields = gson.toJson(crmList);
				leads.setCrm(additionalFields);
				leads.setAssignedTo(list.get(j).get(5));
				leads.setStatus("ACTIVE");
				leads.setFilename(filename);
				leadsArray.add(leads);

			}
			leadsRepository.saveAll(leadsArray);
			LeadVersions lv = new LeadVersions();
			lv.setCampaingName(campaing);
			lv.setFilename(filename);
			lv.setStatus("Y");
			leadVersionsRepository.save(lv);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	// public void createLead(Map<String, String> request) {
	// System.out.println(request);
	// Leads leads = new Leads();
	// leads.setAddress1(request.get("address1"));
	// leads.setAddress2(request.get("address2"));
	// leads.setAddress3(request.get("address3"));
	// leads.setAlternatePhonenumber(request.get("alternatePhonenumber"));
	// leads.setCity(request.get("city"));
	// leads.setCountryCode(request.get("countryCode"));
	// leads.setEmail(request.get("email"));
	// leads.setFirstName(request.get("firstName"));
	// leads.setGender(request.get("gender"));
	// leads.setLastName(request.get("lastName"));
	// leads.setMiddleName(request.get("middleName"));
	// leads.setPhoneNumber(request.get("phoneNumber"));
	// leads.setState(request.get("state"));
	// leads.setPostalCode(request.get("postalCode"));
	// leads.setStatus(request.get("status"));
	// leadRepository.save(leads);
	// }

	public List<Map<String, String>> fetchcountrecordingreportdatabetween(Map<String, Object> request,String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		String toDate = String.valueOf(request.get("dateto"));
		String fromDate = String.valueOf(request.get("datefrom"));
		System.out.println(request);
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateTo = dateFormat.parse(toDate);
			Date dateFrom = dateFormat.parse(fromDate);
			dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
		} catch (Exception e) {

			e.printStackTrace();
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<String> campaings = usersRepository.fetchCampaingOfUser(users, campaing);
		System.out.println(campaings);
		List<Integer> leadsId = campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		System.out.println(leadsId);
		List<Map<String,String>> response = new ArrayList<>();
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet sheet = workbook.createSheet("RecordingReport");
		int rownum = 0;
		int cellnumHeader = 0;
		Row rowHeader = sheet.createRow(rownum++);
		Cell header1 = rowHeader.createCell(cellnumHeader++);
		header1.setCellValue("User");
		Cell header2 = rowHeader.createCell(cellnumHeader++);
		header2.setCellValue("CallEndDate");
		Cell header3 = rowHeader.createCell(cellnumHeader++);
		header3.setCellValue("callDate");
		Cell header4 = rowHeader.createCell(cellnumHeader++);
		header4.setCellValue("Status");
		Cell header6 = rowHeader.createCell(cellnumHeader++);
		header6.setCellValue("Duration");
		Cell header7 = rowHeader.createCell(cellnumHeader++);
		header7.setCellValue("Phone_number");
		Cell header8 = rowHeader.createCell(cellnumHeader++);
		header8.setCellValue("Phone_book");
		Cell header9 = rowHeader.createCell(cellnumHeader++);
		header9.setCellValue("Recording_file");
		Cell header10 = rowHeader.createCell(cellnumHeader++);
		header10.setCellValue("Campaing");
		for(Object[] items:recordingRepository.fetchRecordingsByLeadIds(leadsId)) {
//		recordingRepository.fetchRecordingsByLeadIds(leadsId).forEach((items) -> {
//				Full_Name	Campaugn	Phone_number	callDate	CallEndDate	 Duration	Phone_book	Status	HangupCause	Recording_file

			Row row = sheet.createRow(rownum++);
			int cellnum = 0;
			Cell cell1 = row.createCell(cellnum++);
			cell1.setCellValue(String.valueOf(items[0]));
			Cell cell2 = row.createCell(cellnum++);
			cell2.setCellValue(String.valueOf(items[1]));
			Cell cell3 = row.createCell(cellnum++);
			cell3.setCellValue(String.valueOf(items[2]));
			Cell cell4 = row.createCell(cellnum++);
			cell4.setCellValue(String.valueOf(items[3]));
			Cell cell6 = row.createCell(cellnum++);
			cell6.setCellValue(String.valueOf(items[4]));
			Cell cell7 = row.createCell(cellnum++);
			cell7.setCellValue(String.valueOf(items[5]));
			Cell cell8 = row.createCell(cellnum++);
			cell8.setCellValue(String.valueOf(items[6]));
			Cell cell9 = row.createCell(cellnum++);
			cell9.setCellValue(String.valueOf(items[7]));
			Cell cell10 = row.createCell(cellnum++);
			cell10.setCellValue(String.valueOf(items[8]));
			Map<String, String> responseMap = new HashMap<>();
			responseMap.put("USER", String.valueOf(items[0]));
			responseMap.put("CallEndDate", String.valueOf(items[1]));
			responseMap.put("callDate", String.valueOf(items[2]));
			responseMap.put("Status", String.valueOf(items[3]));
			responseMap.put("Duration", String.valueOf(items[4]));
			responseMap.put("Recording_file", String.valueOf(items[5]));
			responseMap.put("Phone_number", String.valueOf(items[6]));
			responseMap.put("Phone_book", String.valueOf(items[7]));
			response.add(responseMap);
			
		}

		
		File excelFile = new File(reportingLocation + "RecordingReport.xlsx");
		OutputStream fos;
		try {
			fos = new FileOutputStream(excelFile);
			workbook.write(fos);
			fos.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return response;
	}

	public List<Attendance> fetchcountattendancereportdatabetween(Map<String, Object> request, String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		String toDate = String.valueOf(request.get("dateto"));
		String fromDate = String.valueOf(request.get("datefrom"));
		System.out.println(request);
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			Date dateTo = dateFormat.parse(toDate);
			Date dateFrom = dateFormat.parse(fromDate);
			dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
		} catch (Exception e) {

			e.printStackTrace();
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);

		List<String> userNames = usersRepository.fetchUserNameByCampaingNames(campaing);
		List<Attendance> userAttendance;
		if (users.isEmpty()) {
			userAttendance = attendanceRepository.findAttendanceByUserName(userNames, dateFromTimestamp,
					dateToTimestamp);
		} else {
			userAttendance = attendanceRepository.findAttendanceByUserName(users, dateFromTimestamp, dateToTimestamp);
		}
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("MISReport");
			int rownum = 0;
			int cellnumHeader = 0;
			Row rowHeader = sheet.createRow(rownum++);
			Cell header1 = rowHeader.createCell(cellnumHeader++);
			header1.setCellValue("User");
			Cell header2 = rowHeader.createCell(cellnumHeader++);
			header2.setCellValue("Login Time");
			Cell header3 = rowHeader.createCell(cellnumHeader++);
			header3.setCellValue("Logout Time");
			Cell header4 = rowHeader.createCell(cellnumHeader++);
			header4.setCellValue("Work Hour");
			for (Attendance attendance : userAttendance) {
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue((String) attendance.getUsername());
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue(String.valueOf(attendance.getLoggedInTime()));
				Cell cell3 = row.createCell(cellnum++);
				cell3.setCellValue((String) String.valueOf(attendance.getLoggedOutTime()));
				Cell cell4 = row.createCell(cellnum++);
				cell4.setCellValue((String) attendance.getTotalWorkHour());
				Cell cell6 = row.createCell(cellnum++);
			}
			File excelFile = new File(reportingLocation + "AttendanceReport.xlsx");
			OutputStream fos = new FileOutputStream(excelFile);
			workbook.write(fos);
			fos.close();
		}catch (Exception e) {
			e.printStackTrace();
		}
		return userAttendance;
	}

	// WORK IN PROGRESS
	public Map<String, String> fetchAttendancereportdatabetween(Map<String, Object> request, String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		Timestamp dateToTimestamp = null;
		Timestamp dateFromTimestamp = null;
		Map<String, String> response = new HashMap<>();
		try {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
			Date dateTo = dateFormat.parse(String.valueOf(request.get("dateto")));
			Date dateFrom = dateFormat.parse(String.valueOf(request.get("datefrom")));
			dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
			dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
			response.put("status", "true");
		} catch (Exception e) { // this generic but you can control another types of exception
			// look the origin of excption
			response.put("status", "false");
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<String> userNames = usersRepository.fetchUserNameByCampaingNames(campaing);
		List<Attendance> userAttendance;
		if (users.isEmpty()) {
			userAttendance = attendanceRepository.findAttendanceByUserName(userNames, dateFromTimestamp,
					dateToTimestamp);
		} else {
			userAttendance = attendanceRepository.findAttendanceByUserName(users, dateFromTimestamp, dateToTimestamp);
		}
		
		return response;
	}

	public MultiMap<String, Map<String, Object>> fetchcountreportdatabetween(Map<String, Object> request) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		List<String> campaings = usersRepository.fetchCampaingOfUser(users, campaing);
		System.out.println(campaings);
		List<Integer> leadsId = campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		// List<Map<String, Map<String, Object>>> resultArray = new ArrayList<>();
		System.out.println(leadsId);
		String dateTo = String.valueOf(request.get("dateto"));
		String dateFrom = String.valueOf(request.get("datefrom"));
//		call_date >=  TO_TIMESTAMP(:toDate,'YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP(:fromDate,'YYYY-MM-DD HH24:MI:SS')
		System.out.println(
				"select name,status,count(status) as count from leads where  call_date >= TO_TIMESTAMP('"
						+ dateTo + "','YYYY-MM-DD HH24:MI:SS') and call_end_date <= TO_TIMESTAMP('" + dateFrom
						+ "','YYYY-MM-DD HH24:MI:SS') and id IN (1,2) GROUP BY name,status");
		// Map<String,List<Map<String,Object>>> resultUsers = new HashMap<>();
		// MultivaluedHashMap<String, Map<String,Object>> resultUsers = new
		// MultivaluedHashMap<>();
		MultiMap<String, Map<String, Object>> resultUsers = new MultiValueMap<>();
		callLogsRepository.fetchcountreportdatabetween(leadsId, String.valueOf(request.get("dateto")),
				String.valueOf(request.get("datefrom"))).forEach((items) -> {
					System.out.println(items[0]);
					System.out.println(items[1]);
					System.out.println(items[2]);
					System.out.println("#######");
					Map<String, Object> rslt = new HashMap<>();
					rslt.put(String.valueOf(items[1]).replaceAll("\\s", ""), items[2]);
					// rsltArray.add(rslt);
					resultUsers.put(String.valueOf(items[0]), rslt);
					System.out.println(resultUsers);
					// resultArray.add(resultUsers);
				});
		// Map<String,Map<String,Integer>> resultmap = new HashMap<>();
		// for(Map.Entry<String,Object> items:resultUsers.entrySet()) {
		// String[] keys=items.getKey().split("=");
		// Map<String,Integer> rslt = new HashMap<>();
		// rslt.put(keys[1], (Integer)items.getValue());
		// resultmap.put(keys[0], rslt);
		// }
		return resultUsers;
	}

	public MultiMap<String, Map<String, Object>> fetchcountreportdatabetweenByUser(Map<String, Object> request) {
		String userName = String.valueOf(request.get("userName"));
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByUsernameName(userName);
		//// List<Map<String, Map<String, Object>>> resultArray = new ArrayList<>();
		// System.out.println(leadsId);
		// String dateTo = String.valueOf(request.get("dateto"));
		// String dateFrom = String.valueOf(request.get("datefrom"));
		// System.out.println(
		// "select name,status,count(status) as count from leads where date_modified
		// BETWEEN TO_TIMESTAMP('"
		// + dateTo + "','YYYY-MM-DD HH24:MI:SS') and TO_TIMESTAMP('" + dateFrom
		// + "','YYYY-MM-DD HH24:MI:SS') and id IN (1,2) GROUP BY name,status");
		// Map<String,List<Map<String,Object>>> resultUsers = new HashMap<>();
		// MultivaluedHashMap<String, Map<String,Object>> resultUsers = new
		// MultivaluedHashMap<>();
		MultiMap<String, Map<String, Object>> resultUsers = new MultiValueMap<>();
		callLogsRepository.fetchcountreportdatabetweenWithUsers(userName, String.valueOf(request.get("dateto")),
				String.valueOf(request.get("datefrom"))).forEach((items) -> {
					System.out.println(items[0]);
					System.out.println(items[1]);
					System.out.println(items[2]);
					System.out.println("#######");
					Map<String, Object> rslt = new HashMap<>();
					rslt.put(String.valueOf(items[1]).replaceAll("\\s", ""), items[2]);
					// rsltArray.add(rslt);
					resultUsers.put(String.valueOf(items[0]), rslt);
					System.out.println(resultUsers);
					// resultArray.add(resultUsers);
				});
		

		// Map<String,Map<String,Integer>> resultmap = new HashMap<>();
		// for(Map.Entry<String,Object> items:resultUsers.entrySet()) {
		// String[] keys=items.getKey().split("=");
		// Map<String,Integer> rslt = new HashMap<>();
		// rslt.put(keys[1], (Integer)items.getValue());
		// resultmap.put(keys[0], rslt);
		// }
		return resultUsers;
	}

	public void feedbackLeadsMutiple(List<Map<String, String>> request) {
		List<Leads> listUsers = new ArrayList<>();
		List<CallLogs> listCallLogs = new ArrayList<>();

		request.forEach(items -> {
			Leads leads = leadRepository.findById(Integer.parseInt(items.get("leadId"))).get();
			CallLogs callLogs = new CallLogs();
			leads.setStatus(items.get("status"));
			String callStartTime="";
			String callEndTime="";
			try {
				callStartTime = LeadsService.formatDate(items.get("callStartedTime"), "dd-MM-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				callEndTime = LeadsService.formatDate(items.get("callEndTime"), "dd-MM-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
				
			} catch (ParseException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println(callStartTime);
			Date startTimeCall = null;
			Date endTimeCall = null;
			try {
				startTimeCall = dateFormat.parse(callStartTime);
				endTimeCall = dateFormat.parse(callEndTime);
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			System.out.println("###############batch feedback");
			System.out.println(startTimeCall);
			
			leads.setName(items.get("username"));
			listUsers.add(leads);
			callLogs.setAssignedTo(items.get("username"));
			callLogs.setCallDate(startTimeCall);
			callLogs.setCallEndDate(endTimeCall);
			callLogs.setCallBackDateTime(items.get("callBackDateTime"));
			callLogs.setComments(items.get("comment"));
			callLogs.setCallDuration(items.get("callTime"));	
			callLogs.setLeadId(leads.getId());
			callLogs.setStatus(leads.getStatus());
			listCallLogs.add(callLogs);
		});
		leadRepository.saveAll(listUsers);
		callLogsRepository.saveAll(listCallLogs);
		
	}
	public static String formatDate (String date, String initDateFormat, String endDateFormat) throws ParseException {

	    Date initDate = new SimpleDateFormat(initDateFormat).parse(date);
	    SimpleDateFormat formatter = new SimpleDateFormat(endDateFormat);
	    String parsedDate = formatter.format(initDate);

	    return parsedDate;
	}

	public Map<String, List<CallLogs>> fetchreportdatabetweenWithUserName(Map<String, Object> request) {
		Map<String, List<CallLogs>> response = new HashMap<>();
		String userName = String.valueOf(request.get("userName"));
		// List<String> campaing = (List<String>) request.get("campaingName");
		// List<String> campaings = usersRepository.fetchCampaingOfUser(users,
		// campaing);
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		List<CallLogs> resultLeads = callLogsRepository.fetchreportdatabetweenWithUserName(userName);
		response.put("leads", resultLeads);
		// try {
		// ByteArrayOutputStream stream = new ByteArrayOutputStream();
		// XSSFWorkbook workbook = new XSSFWorkbook();
		// XSSFSheet sheet = workbook.createSheet("MISReport");
		// int rownum = 0;
		// int cellnumHeader = 0;
		// Row rowHeader = sheet.createRow(rownum++);
		// Cell header1 = rowHeader.createCell(cellnumHeader++);
		// header1.setCellValue("Assigned User");
		// Cell header2 = rowHeader.createCell(cellnumHeader++);
		// header2.setCellValue("Phone Nmber");
		// Cell header3 = rowHeader.createCell(cellnumHeader++);
		// header3.setCellValue("Status");
		// Cell header4 = rowHeader.createCell(cellnumHeader++);
		// header4.setCellValue("FirstName");
		// Cell header5 = rowHeader.createCell(cellnumHeader++);
		// header5.setCellValue("Address");
		// Cell header6 = rowHeader.createCell(cellnumHeader++);
		// header6.setCellValue("Comments");
		// Cell header7 = rowHeader.createCell(cellnumHeader++);
		// header7.setCellValue("Call Duration");
		// Cell header8 = rowHeader.createCell(cellnumHeader++);
		// header8.setCellValue("Call Date");
		// for (Leads lead : resultLeads) {
		// // this creates a new row in the sheet
		// Row row = sheet.createRow(rownum++);
		// int cellnum = 0;
		// Cell cell1 = row.createCell(cellnum++);
		// cell1.setCellValue((String) lead.getName());
		// Cell cell2 = row.createCell(cellnum++);
		// cell2.setCellValue((String) lead.getPhoneNumber());
		// Cell cell3 = row.createCell(cellnum++);
		// cell3.setCellValue((String) lead.getStatus());
		// Cell cell4 = row.createCell(cellnum++);
		// cell4.setCellValue((String) lead.getFirstName());
		// Cell cell5 = row.createCell(cellnum++);
		// cell5.setCellValue((String) lead.getAddress1());
		// Cell cell6 = row.createCell(cellnum++);
		// cell6.setCellValue((String) lead.getComments());
		// Cell cell7 = row.createCell(cellnum++);
		// cell7.setCellValue((String) lead.getLastLocalCallTime());
		// Cell cell8 = row.createCell(cellnum++);
		// cell8.setCellValue((String.valueOf(lead.getDateModified())));
		// }
		// File excelFile = new File(reportingLocation + "MISReport.xlsx");
		// OutputStream fos = new FileOutputStream(excelFile);
		// workbook.write(fos);
		// fos.close();
		// System.out.println("MISReport.xlsx written successfully on disk.");
		// FileInputStream fis = new FileInputStream(excelFile);
		// byte[] buf = new byte[1024];
		// try {
		// for (int readNum; (readNum = fis.read(buf)) != -1;) {
		// stream.write(buf, 0, readNum); // no doubt here is 0
		// System.out.println("read " + readNum + " bytes,");
		// }
		// } catch (IOException ex) {
		// ex.printStackTrace();
		// }
		// byte[] bytes = stream.toByteArray();
		//
		// HttpHeaders header = new HttpHeaders();
		// header.setContentType(new MediaType("application", "force-download"));
		// header.set(HttpHeaders.CONTENT_DISPOSITION, "attachment;
		// filename=ProductTemplate.xlsx");
		// return new ByteArrayResource(bytes);
		// } catch (Exception e) {
		// e.printStackTrace();
		// }
		return response;
	}

	public ByteArrayResource fetchreportdatabetween(Map<String, Object> request, String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		List<String> campaings = usersRepository.fetchCampaingOfUser(users, campaing);
		List<Integer> leadsId = campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		List<Object[]> resultLeads = callLogsRepository.fetchreportdatabetween(leadsId, String.valueOf(request.get("dateto")),
				String.valueOf(request.get("datefrom")));
		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("MISReport");
			int rownum = 0;
			int cellnumHeader = 0;
			Row rowHeader = sheet.createRow(rownum++);
			Cell header1 = rowHeader.createCell(cellnumHeader++);
			header1.setCellValue("Assigned User");
			Cell header2 = rowHeader.createCell(cellnumHeader++);
			header2.setCellValue("Phone Nmber");
			Cell header3 = rowHeader.createCell(cellnumHeader++);
			header3.setCellValue("Status");
			Cell header4 = rowHeader.createCell(cellnumHeader++);
			header4.setCellValue("FirstName");
			Cell header6 = rowHeader.createCell(cellnumHeader++);
			header6.setCellValue("Comments");
			Cell header7 = rowHeader.createCell(cellnumHeader++);
			header7.setCellValue("Call Duration");
			Cell header8 = rowHeader.createCell(cellnumHeader++);
			header8.setCellValue("Call Date");
			for (Object[] lead : resultLeads) {
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue((String) lead[0]);
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue((String) lead[0]);
				Cell cell3 = row.createCell(cellnum++);
				cell3.setCellValue((String) lead[0]);
				Cell cell4 = row.createCell(cellnum++);
				cell4.setCellValue((String) lead[0]);
				Cell cell6 = row.createCell(cellnum++);
				cell6.setCellValue((String) lead[0]);
				Cell cell7 = row.createCell(cellnum++);
				cell7.setCellValue((String) lead[0]);
				Cell cell8 = row.createCell(cellnum++);
				cell8.setCellValue((String.valueOf(lead[0])));
			}
			File excelFile = new File(reportingLocation + "MISReport.xlsx");
			OutputStream fos = new FileOutputStream(excelFile);
			workbook.write(fos);
			fos.close();
			System.out.println("MISReport.xlsx written successfully on disk.");
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

	public void feedback(int id, String status, String calltime) {
		Leads leads = leadRepository.findById(id).get();
		leads.setStatus(status);
//		leads.setLastLocalCallTime(calltime);
		leadRepository.save(leads);
		
	}

	public void feedbackLead(int id, String status, String calltime, String comments, String callBackDateTime,
			String callStartedTime, String callEndTime,String username) {
		Leads leads = leadRepository.findById(id).get();
		String callEnd="";
		String callStart="";
		leads.setStatus(status);
		try {
			callStart = LeadsService.formatDate(callStartedTime, "dd-MM-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		
			 callEnd = LeadsService.formatDate(callEndTime, "dd-MM-yyyy HH:mm:ss", "yyyy-MM-dd HH:mm:ss");
		} catch (ParseException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Date startTimeCall = null;
		Date endTimeCall = null;
		System.out.println("############### feedback");
		
		try {
			if(callStartedTime!=null) {
			startTimeCall = dateFormat.parse(callStart);
			System.out.println(startTimeCall);
			}
			if(callEndTime!=null) {
			endTimeCall = dateFormat.parse(callEnd);
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(username!=null) {
			leads.setName(username);
		}
		leadRepository.save(leads);
		CallLogs callLogs = new CallLogs();
		callLogs.setLeadId(leads.getId());
		callLogs.setAssignedTo(leads.getName());
		callLogs.setCallDate(startTimeCall);
		callLogs.setCallDuration(calltime);
		callLogs.setCallEndDate(endTimeCall);
		callLogs.setComments(comments);
		callLogs.setStatus(status);
		callLogsRepository.save(callLogs);
	}

	// public void createLeadWithCampaing(Map<String, String> request) {
	// Leads leads = new Leads();
	// MapString
	// leads.setAddress1(request.get("address1"));
	// leads.setAddress2(request.get("address2"));
	// leads.setAddress3(request.get("address3"));
	// leads.setAlternatePhonenumber(request.get("alternatePhonenumber"));
	// leads.setCity(request.get("city"));
	// leads.setCountryCode(request.get("countryCode"));
	// leads.setEmail(request.get("email"));
	// leads.setFirstName(request.get("firstName"));
	// leads.setGender(request.get("gender"));
	// leads.setLastName(request.get("lastName"));
	// leads.setMiddleName(request.get("middleName"));
	// leads.setPhoneNumber(request.get("phoneNumber"));
	// leads.setState(request.get("state"));
	// leads.setPostalCode(request.get("postalCode"));
	// leads.setStatus(request.get("status"));
	// leadRepository.save(leads);
	// }

	public List<Leads> fetchLeadByPhoneNumber(String phoneNumber) {
		return leadRepository.findLeadByNumber(phoneNumber);
	}

	// public void attachLeadToCampaing(Map<String, String> request) {
	// Optional<Leads> leads =
	// leadRepository.findById(Integer.parseInt(request.get("lead_id")));
	// if (leads.isPresent()) {
	// Leads lead = leads.get();
	// Optional<Campaing> campaing =
	// campaingRepository.findById(Integer.parseInt(request.get("campaing_id")));
	// if (campaing.isPresent()) {
	// Campaing campaingFound = new Campaing();
	// CampaingLeadMapping campaingLeadMapping = new CampaingLeadMapping();
	// campaingLeadMapping.setCampaingid(campaingFound.getId());
	// campaingLeadMapping.setLeadid(lead.getId());
	// campaingLeadMappingRepository.save(campaingLeadMapping);
	// }
	// }
	// }
}

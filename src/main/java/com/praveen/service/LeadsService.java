package com.praveen.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.sql.Timestamp;
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
import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.GroupCampaingMappingRepository;
import com.praveen.dao.LeadVersionsRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.dao.UserGroupRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Attendance;
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

	public List<Leads> fetchAllLeads() {
		return leadRepository.findAll();
	}
	public List<String> findLeadByCampaingName(String campaingName){
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
				Map<String, String> crm = new HashMap<>();

				for (int i = 6; i < list.get(j).size(); i++) {
					crm.put(headers.get(i).replaceAll(" ", "_"), list.get(j).get(i));
				}

				Leads leads = new Leads();
				leads.setPhoneNumber(list.get(j).get(0));
				leads.setFirstName(list.get(j).get(1));
				leads.setState(list.get(j).get(2));
				leads.setCity(list.get(j).get(3));
				leads.setEmail(list.get(j).get(4));
				Gson gson = new Gson();
				String additionalFields = gson.toJson(crm);
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

	public List<Attendance> fetchcountattendancereportdatabetween(Map<String, Object> request) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		Timestamp dateToTimestamp=null;
		Timestamp dateFromTimestamp=null;
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		    Date dateTo = dateFormat.parse(String.valueOf(request.get("dateto")));
		    Date dateFrom = dateFormat.parse(String.valueOf(request.get("datefrom")));
		    dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
		    dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
		} catch(Exception e) { //this generic but you can control another types of exception
		    // look the origin of excption 
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<String> userNames= usersRepository.fetchUserNameByCampaingNames(campaing);
		List<Attendance> userAttendance=attendanceRepository.findAttendanceByUserName(userNames,dateFromTimestamp,dateToTimestamp);
		return userAttendance;
	}
	
	//WORK IN PROGRESS
	public Map<String,String> fetchAttendancereportdatabetween(Map<String, Object> request,String reportingLocation) {
		List<String> users = (List<String>) request.get("userName");
		List<String> campaing = (List<String>) request.get("campaingName");
		Timestamp dateToTimestamp=null;
		Timestamp dateFromTimestamp=null;
		Map<String,String> response = new HashMap<>();
		try {
		    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
		    Date dateTo = dateFormat.parse(String.valueOf(request.get("dateto")));
		    Date dateFrom = dateFormat.parse(String.valueOf(request.get("datefrom")));
		    dateToTimestamp = new java.sql.Timestamp(dateTo.getTime());
		    dateFromTimestamp = new java.sql.Timestamp(dateFrom.getTime());
		    response.put("status","true");
		} catch(Exception e) { //this generic but you can control another types of exception
		    // look the origin of excption 
			response.put("status","false");
		}
		System.out.println(dateToTimestamp);
		System.out.println(dateFromTimestamp);
		List<String> userNames= usersRepository.fetchUserNameByCampaingNames(campaing);
		List<Attendance> userAttendance=attendanceRepository.findAttendanceByUserName(userNames,dateFromTimestamp,dateToTimestamp);
		
		
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
		System.out.println(
				"select name,status,count(status) as count from leads where  date_modified BETWEEN  TO_TIMESTAMP('"
						+ dateTo + "','YYYY-MM-DD HH24:MI:SS') and TO_TIMESTAMP('" + dateFrom
						+ "','YYYY-MM-DD HH24:MI:SS') and id IN (1,2) GROUP BY name,status");
		// Map<String,List<Map<String,Object>>> resultUsers = new HashMap<>();
		// MultivaluedHashMap<String, Map<String,Object>> resultUsers = new
		// MultivaluedHashMap<>();
		MultiMap<String, Map<String, Object>> resultUsers = new MultiValueMap<>();
		leadRepository.fetchcountreportdatabetween(leadsId, String.valueOf(request.get("dateto")),
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
		leadRepository.fetchcountreportdatabetweenWithUsers(userName, String.valueOf(request.get("dateto")),
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

		request.forEach(items -> {
			Leads leads = leadRepository.findById(Integer.parseInt(items.get("leadId"))).get();
			leads.setStatus(items.get("status"));
			leads.setLastLocalCallTime(items.get("callTime"));
			leads.setCallBackDateTime(items.get("callBackDateTime"));
			leads.setComments(items.get("comment"));
			listUsers.add(leads);
		});
		leadRepository.saveAll(listUsers);
	}

	public Map<String, List<Leads>> fetchreportdatabetweenWithUserName(Map<String, Object> request) {
		Map<String, List<Leads>> response = new HashMap<>();
		String userName = String.valueOf(request.get("userName"));
		// List<String> campaing = (List<String>) request.get("campaingName");
		// List<String> campaings = usersRepository.fetchCampaingOfUser(users,
		// campaing);
		// List<Integer> leadsId =
		// campaingLeadMappingRepository.findLeadsByCampaingName(campaings);
		List<Leads> resultLeads = leadRepository.fetchreportdatabetweenWithUserName(userName,
				String.valueOf(request.get("dateto")), String.valueOf(request.get("datefrom")));
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
		List<Leads> resultLeads = leadRepository.fetchreportdatabetween(leadsId, String.valueOf(request.get("dateto")),
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
			for (Leads lead : resultLeads) {
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue((String) lead.getName());
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue((String) lead.getPhoneNumber());
				Cell cell3 = row.createCell(cellnum++);
				cell3.setCellValue((String) lead.getStatus());
				Cell cell4 = row.createCell(cellnum++);
				cell4.setCellValue((String) lead.getFirstName());
				Cell cell6 = row.createCell(cellnum++);
				cell6.setCellValue((String) lead.getComments());
				Cell cell7 = row.createCell(cellnum++);
				cell7.setCellValue((String) lead.getLastLocalCallTime());
				Cell cell8 = row.createCell(cellnum++);
				cell8.setCellValue((String.valueOf(lead.getDateModified())));
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
		leads.setLastLocalCallTime(calltime);
		leadRepository.save(leads);
	}

	public void feedbackLead(int id, String status, String calltime, String comments, String callBackDateTime) {
		Leads leads = leadRepository.findById(id).get();
		leads.setStatus(status);
		leads.setLastLocalCallTime(calltime);
		leads.setCallBackDateTime(callBackDateTime);
		leads.setComments(comments);
		leadRepository.save(leads);
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

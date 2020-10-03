package com.praveen.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

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
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.praveen.dao.ImagesRepository;
import com.praveen.dao.InterestedRepository;
import com.praveen.dao.MatchingRequirementsRepository;
import com.praveen.dao.PropertiesDetailsRepository;
import com.praveen.dao.SubscriptionRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Images;
import com.praveen.model.Interested;
import com.praveen.model.PropertiesDetails;
import com.praveen.model.Subscription;
import com.praveen.model.Users;
import com.praveen.model.MatchingRequirements;

@Service
public class PropertiesDetailsService {
	@Autowired
	PropertiesDetailsRepository propertiesDetailsRepository;
	@Autowired
	ImagesRepository imagesRepository;
	@Autowired
	InterestedRepository interestedRepository;
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	SubscriptionRepository subscriptionRepository;
	@Autowired
	MatchingRequirementsRepository matchingRequirementsRepository;
	@PersistenceContext
	public EntityManager em;

	public List<Map<String, Object>> manageProperties(Map<String, String> respo) {
		String email = respo.get("email");
		Users users=usersRepository.findByEmail(email);
		List<Map<String, Object>> response = new ArrayList<>();
		propertiesDetailsRepository.findByEmail(email).forEach((item) -> {
			List<Images> images = imagesRepository.getImageByPropertyId((Integer) item.getId());
			System.out.println(item);
			Map<String, Object> prop = new HashMap<>();
			prop.put("id", item.getId());
			prop.put("name", item.getName());
			prop.put("address", item.getAddress());
			prop.put("bedroom", item.getBedroom());
			prop.put("washroom", item.getWashroom());
			prop.put("garage", item.getGarage());
			prop.put("description", item.getDescription());
			prop.put("area", item.getArea());
			prop.put("ownerEmail", item.getOwnerEmail());
			prop.put("isavailable", item.getIsavailable());
			prop.put("phoneNumber", item.getPhoneNumber().replaceAll(".(?=.{4})", "*"));
			prop.put("frontImage", item.getFrontImage());
			prop.put("latitude", item.getLatitude());
			prop.put("longitude", item.getLongitude());
			prop.put("property_type", item.getPropertyType());
			prop.put("allowed", item.getAllowed());
			prop.put("furnish", item.getFurnish());
			prop.put("dateCreated", item.getDateCreated());
			prop.put("DateModified", item.getDateModified());
			prop.put("price", item.getPrice());
			prop.put("maintainance", item.getMaintainance());
			prop.put("security", item.getSecurity());
			prop.put("images", images);
			response.add(prop);
		});
		return response;
	}

	public void updatePropertyStatus(int propertyId, int status) {
		PropertiesDetails pd = propertiesDetailsRepository.findById(propertyId).get();
		if (pd != null) {
			pd.setIsavailable(status);
			propertiesDetailsRepository.save(pd);
		}

	}

	public void deleteInterestedProperties(Integer propertyId, String email) {
		interestedRepository.deleteInterestedProperties(propertyId, email);
	}
	public static <T, U> List<U> 
    convertStringListToIntList(List<T> listOfString, 
                               Function<T, U> function) 
    { 
        return listOfString.stream() 
            .map(function) 
            .collect(Collectors.toList()); 
    } 
	public List<Map<String, Object>> sortlistedProperties(String email) {
		System.out.println(email);
		List<Map<String, Object>> response = new ArrayList<>();

		List<String> propertiesId=interestedRepository.findByEmail(email);
		System.out.println(propertiesId);
		 List<Integer> propertiesIdInteger = convertStringListToIntList( 
				 propertiesId, 
		            Integer::parseInt); 
		propertiesDetailsRepository.findAllById(propertiesIdInteger).forEach((item) -> {
			List<Images> images = imagesRepository.getImageByPropertyId((Integer) item.getId());
			System.out.println(images.size());
			Map<String, Object> prop = new HashMap<>();
			prop.put("id", item.getId());
			prop.put("name", item.getName());
			prop.put("address", item.getAddress());
			prop.put("bedroom", item.getBedroom());
			prop.put("washroom", item.getWashroom());
			prop.put("garage", item.getGarage());
			prop.put("description", item.getDescription());
			prop.put("area", item.getArea());
			prop.put("ownerEmail", item.getOwnerEmail());
			prop.put("isavailable", item.getIsavailable());
			prop.put("phoneNumber", item.getPhoneNumber().replaceAll(".(?=.{4})", "*"));
			prop.put("frontImage", item.getFrontImage());
			prop.put("latitude", item.getLatitude());
			prop.put("longitude", item.getLongitude());
			prop.put("property_type", item.getPropertyType());
			prop.put("allowed", item.getAllowed());
			prop.put("furnish", item.getFurnish());
			prop.put("dateCreated", item.getDateCreated());
			prop.put("DateModified", item.getDateModified());
			prop.put("price", item.getPrice());
			prop.put("maintainance", item.getMaintainance());
			prop.put("security", item.getSecurity());
			prop.put("images", images);
			response.add(prop);
		});
		return response;
	}

	public List<String> searchAddress(String address) {
		List<String> searchDetails = new ArrayList<>();
		propertiesDetailsRepository.searchAddress(address).forEach((item) -> {
			searchDetails.add((String) item[1]);
			searchDetails.add((String) item[0]);
		});
		return searchDetails;
	}

	public ByteArrayResource fetchreportdatabetweenpropertyadded(Map<String, Object> request,
			String reportingLocation) {
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
		List<PropertiesDetails> resultLeads = new ArrayList<>();
		resultLeads.addAll(
				propertiesDetailsRepository.fetchreportdatabetweenpropertyadded(dateFromTimestamp, dateToTimestamp));

		try {
			ByteArrayOutputStream stream = new ByteArrayOutputStream();
			XSSFWorkbook workbook = new XSSFWorkbook();
			XSSFSheet sheet = workbook.createSheet("PropertiesReport");
			int rownum = 0;
			int cellnumHeader = 0;
			Row rowHeader = sheet.createRow(rownum++);
			Cell header1 = rowHeader.createCell(cellnumHeader++);
			header1.setCellValue("name");
			Cell header2 = rowHeader.createCell(cellnumHeader++);
			header2.setCellValue("owner_email");
			Cell header3 = rowHeader.createCell(cellnumHeader++);
			header3.setCellValue("phone_number");
			Cell header4 = rowHeader.createCell(cellnumHeader++);
			header4.setCellValue("area");
			Cell header5 = rowHeader.createCell(cellnumHeader++);
			header5.setCellValue("date_created");
			Cell header6 = rowHeader.createCell(cellnumHeader++);
			header6.setCellValue("isavailable");
			Cell header7 = rowHeader.createCell(cellnumHeader++);
			header7.setCellValue("latitude");
			Cell header8 = rowHeader.createCell(cellnumHeader++);
			header8.setCellValue("longitude");
			Cell header9 = rowHeader.createCell(cellnumHeader++);
			header9.setCellValue("address");
			Cell header10 = rowHeader.createCell(cellnumHeader++);
			header10.setCellValue("locality");
			Cell header11 = rowHeader.createCell(cellnumHeader++);
			header11.setCellValue("city");
			Cell header12 = rowHeader.createCell(cellnumHeader++);
			header12.setCellValue("state");
			Cell header13 = rowHeader.createCell(cellnumHeader++);
			header13.setCellValue("country");
			Cell header14 = rowHeader.createCell(cellnumHeader++);
			header14.setCellValue("furnish");
			Cell header15 = rowHeader.createCell(cellnumHeader++);
			header15.setCellValue("allowed");
			Cell header16 = rowHeader.createCell(cellnumHeader++);
			header16.setCellValue("property_type");
			Cell header17 = rowHeader.createCell(cellnumHeader++);
			header17.setCellValue("price");
			Cell header18 = rowHeader.createCell(cellnumHeader++);
			header18.setCellValue("maintainance");
			Cell header19 = rowHeader.createCell(cellnumHeader++);
			header19.setCellValue("security");
			Cell header20 = rowHeader.createCell(cellnumHeader++);
			header20.setCellValue("isApproved");
			Cell header21 = rowHeader.createCell(cellnumHeader++);
			header21.setCellValue("property Id");
			CreationHelper createHelper = workbook.getCreationHelper();
			for (PropertiesDetails properties : resultLeads) {
				// System.out.println(properties.getFullName());
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue(properties.getName());
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue(properties.getOwnerEmail());
				Cell cell3 = row.createCell(cellnum++);
				cell3.setCellValue(properties.getPhoneNumber());
				Cell cell4 = row.createCell(cellnum++);
				cell4.setCellValue(properties.getArea());
				DateFormat dateFormat = new SimpleDateFormat("yyyy-mm-dd HH:mm:ss");
				String strDate = dateFormat.format(properties.getDateCreated());
				System.out.println("Converted Date: " + strDate);
				Cell cell5 = row.createCell(cellnum++);
				cell5.setCellValue(strDate);
				Cell cell6 = row.createCell(cellnum++);
				cell6.setCellValue(properties.getIsavailable());
				Cell cell7 = row.createCell(cellnum++);
				cell7.setCellValue(properties.getLatitude());
				Cell cell8 = row.createCell(cellnum++);
				cell8.setCellValue(properties.getLongitude());
				Cell cell9 = row.createCell(cellnum++);
				cell9.setCellValue(properties.getAddress());
				Cell cell10 = row.createCell(cellnum++);
				cell10.setCellValue(properties.getLocality());
				Cell cell11 = row.createCell(cellnum++);
				cell11.setCellValue(properties.getCity());
				Cell cell12 = row.createCell(cellnum++);
				cell12.setCellValue(properties.getState());
				Cell cell13 = row.createCell(cellnum++);
				cell13.setCellValue(properties.getCountry());
				Cell cell14 = row.createCell(cellnum++);
				cell14.setCellValue(properties.getFurnish());
				Cell cell15 = row.createCell(cellnum++);
				cell15.setCellValue(properties.getAllowed());
				Cell cell16 = row.createCell(cellnum++);
				cell16.setCellValue(properties.getPropertyType());
				Cell cell17 = row.createCell(cellnum++);
				cell17.setCellValue(properties.getPrice());
				Cell cell18 = row.createCell(cellnum++);
				cell18.setCellValue(properties.getMaintainance());
				Cell cell19 = row.createCell(cellnum++);
				cell19.setCellValue(properties.getSecurity());
				Cell cell20 = row.createCell(cellnum++);
				cell20.setCellValue(properties.getIsApproved());
				Cell cell21 = row.createCell(cellnum++);
				cell21.setCellValue(properties.getId());
			}
			File excelFile = new File(reportingLocation + "PropertiesReport.xlsx");
			OutputStream fos = new FileOutputStream(excelFile);
			workbook.write(fos);
			fos.close();
			System.out.println("PropertiesReport.xlsx written successfully on disk.");
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

	public List<Map<String, Object>> mainProperties(Map<String, String> request) {
		List<PropertiesDetails> propertiesDetails = new ArrayList<>();
		if (request.get("priceRange") == null) {
			if (request.get("address") == null) {
				if (request.get("type") != null) {
					propertiesDetailsRepository.getNearByPropertyByType(request.get("latitude"),
							request.get("longitude"), request.get("type")).forEach(item -> {
								PropertiesDetails propertiesDetail = new PropertiesDetails();
								propertiesDetail.setId((Integer) item[0]);
								propertiesDetail.setName((String) item[1]);
								propertiesDetail.setAddress((String) item[2]);
								propertiesDetail.setBedroom((String) item[3]);
								propertiesDetail.setWashroom((String) item[4]);
								propertiesDetail.setGarage((String) item[5]);
								propertiesDetail.setDescription((String) item[6]);
								propertiesDetail.setArea(Integer.parseInt(String.valueOf(item[7])));
								propertiesDetail.setOwnerEmail((String) item[8]);
								propertiesDetail.setIsavailable(Integer.parseInt(String.valueOf(item[9])));
								propertiesDetail.setPhoneNumber((String) item[10]);
								propertiesDetail.setFrontImage((String) item[11]);
								propertiesDetail.setLatitude((String) item[12]);
								propertiesDetail.setLongitude((String) item[13]);
								propertiesDetail.setPrice(Integer.parseInt(String.valueOf(item[16])));
								propertiesDetail.setParking((String) item[17]);
								propertiesDetail.setLocality((String) item[18]);
								propertiesDetails.add(propertiesDetail);
							});

				}
			} else {
				if (request.get("type") != null) {
					propertiesDetails.addAll(propertiesDetailsRepository
							.getPropertyByTypeAndAddress(request.get("type"), request.get("address")));
				} else {
					propertiesDetails.addAll(propertiesDetailsRepository.getPropertyByAddress(request.get("address")));
				}
			}
		} else {
			if (request.get("address") != null) {
				if (request.get("type") != null) {
					if (request.get("priceRange").contains(">")) {
						String priceRange = request.get("priceRange").substring(1);
						propertiesDetails.addAll(propertiesDetailsRepository
								.mainPropertiesGreaterPrice(request.get("address"), priceRange, request.get("type")));
					} else if (request.get("priceRange").contains("<")) {
						String priceRange = request.get("priceRange").substring(1);
						propertiesDetails.addAll(propertiesDetailsRepository
								.mainPropertiesLessPrice(request.get("address"), priceRange, request.get("type")));

					} else {
						String[] priceRange = request.get("priceRange").split(",");
						propertiesDetails.addAll(propertiesDetailsRepository.mainPropertiesRangePrice(
								"%" + request.get("address") + "%", priceRange[0], priceRange[1], request.get("type")));

					}
				} else {
					if (request.get("priceRange").contains(">")) {
						String priceRange = request.get("priceRange").substring(1);
						propertiesDetails.addAll(propertiesDetailsRepository
								.getPropertyByGreaterRangeAndType(priceRange, request.get("address")));
					} else if (request.get("priceRange").contains("<")) {
						String priceRange = request.get("priceRange").substring(1);
						System.out.println("########");
						System.out.println(priceRange);
						propertiesDetails.addAll(propertiesDetailsRepository.getPropertyByLessRangeAndType(priceRange,
								request.get("address")));

					} else {
						String[] priceRange = request.get("priceRange").split(",");
						propertiesDetails.addAll(propertiesDetailsRepository.mainPropertiesRangePriceAndAddress(
								"%" + request.get("address") + "%", priceRange[0], priceRange[1]));
					}
				}
			} else {
				if (request.get("type") != null) {
					if (request.get("priceRange").contains(">")) {
						String priceRange = request.get("priceRange").substring(1);
						propertiesDetailsRepository.getNearByPropertyByGreaterRangeAndType(request.get("latitude"),
								request.get("longitude"), priceRange, request.get("type")).forEach(item -> {
									PropertiesDetails propertiesDetail = new PropertiesDetails();
									propertiesDetail.setId((Integer) item[0]);
									propertiesDetail.setName((String) item[1]);
									propertiesDetail.setAddress((String) item[2]);
									propertiesDetail.setBedroom((String) item[3]);
									propertiesDetail.setWashroom((String) item[4]);
									propertiesDetail.setGarage((String) item[5]);
									propertiesDetail.setDescription((String) item[6]);
									propertiesDetail.setArea(Integer.parseInt(String.valueOf(item[7])));
									propertiesDetail.setOwnerEmail((String) item[8]);
									propertiesDetail.setIsavailable(Integer.parseInt(String.valueOf(item[9])));
									propertiesDetail.setPhoneNumber((String) item[10]);
									propertiesDetail.setFrontImage((String) item[11]);
									propertiesDetail.setLatitude((String) item[12]);
									propertiesDetail.setLongitude((String) item[13]);
									propertiesDetail.setPrice(Integer.parseInt(String.valueOf(item[16])));
									propertiesDetail.setParking((String) item[17]);
									propertiesDetail.setLocality((String) item[18]);
									propertiesDetails.add(propertiesDetail);
								});
					} else if (request.get("priceRange").contains("<")) {
						String priceRange = request.get("priceRange").substring(1);
						propertiesDetailsRepository.getNearByPropertyByLessRangeAndType(request.get("latitude"),
								request.get("longitude"), priceRange, request.get("type")).forEach(item -> {
									PropertiesDetails propertiesDetail = new PropertiesDetails();
									propertiesDetail.setId((Integer) item[0]);
									propertiesDetail.setName((String) item[1]);
									propertiesDetail.setAddress((String) item[2]);
									propertiesDetail.setBedroom((String) item[3]);
									propertiesDetail.setWashroom((String) item[4]);
									propertiesDetail.setGarage((String) item[5]);
									propertiesDetail.setDescription((String) item[6]);
									propertiesDetail.setArea(Integer.parseInt(String.valueOf(item[7])));
									propertiesDetail.setOwnerEmail((String) item[8]);
									propertiesDetail.setIsavailable(Integer.parseInt(String.valueOf(item[9])));
									propertiesDetail.setPhoneNumber((String) item[10]);
									propertiesDetail.setFrontImage((String) item[11]);
									propertiesDetail.setLatitude((String) item[12]);
									propertiesDetail.setLongitude((String) item[13]);
									propertiesDetail.setPrice(Integer.parseInt(String.valueOf(item[16])));
									propertiesDetail.setParking((String) item[17]);
									propertiesDetail.setLocality((String) item[18]);
									propertiesDetails.add(propertiesDetail);
								});

					} else {
						System.out.println("priceRange and type");
						String[] priceRange = request.get("priceRange").split(",");
						propertiesDetailsRepository.mainPropertiesRangePriceAndType(request.get("latitude"),
								request.get("longitude"), priceRange[0], priceRange[1], request.get("type"))
								.forEach(item -> {
									PropertiesDetails propertiesDetail = new PropertiesDetails();
									propertiesDetail.setId((Integer) item[0]);
									propertiesDetail.setName((String) item[1]);
									propertiesDetail.setAddress((String) item[2]);
									propertiesDetail.setBedroom((String) item[3]);
									propertiesDetail.setWashroom((String) item[4]);
									propertiesDetail.setGarage((String) item[5]);
									propertiesDetail.setDescription((String) item[6]);
									propertiesDetail.setArea(Integer.parseInt(String.valueOf(item[7])));
									propertiesDetail.setOwnerEmail((String) item[8]);
									propertiesDetail.setIsavailable(Integer.parseInt(String.valueOf(item[9])));
									propertiesDetail.setPhoneNumber((String) item[10]);
									propertiesDetail.setFrontImage((String) item[11]);
									propertiesDetail.setLatitude((String) item[12]);
									propertiesDetail.setLongitude((String) item[13]);
									propertiesDetail.setPrice(Integer.parseInt(String.valueOf(item[16])));
									propertiesDetail.setParking((String) item[17]);
									propertiesDetail.setLocality((String) item[18]);
									propertiesDetails.add(propertiesDetail);
								});

					}
				} else {
					if (request.get("priceRange").contains(">")) {
						String priceRange = request.get("priceRange").substring(1);
						propertiesDetailsRepository.mainPropertiesGreaterPriceOnly(request.get("latitude"),
								request.get("longitude"), priceRange).forEach(item -> {
									PropertiesDetails propertiesDetail = new PropertiesDetails();
									propertiesDetail.setId((Integer) item[0]);
									propertiesDetail.setName((String) item[1]);
									propertiesDetail.setAddress((String) item[2]);
									propertiesDetail.setBedroom((String) item[3]);
									propertiesDetail.setWashroom((String) item[4]);
									propertiesDetail.setGarage((String) item[5]);
									propertiesDetail.setDescription((String) item[6]);
									propertiesDetail.setArea(Integer.parseInt(String.valueOf(item[7])));
									propertiesDetail.setOwnerEmail((String) item[8]);
									propertiesDetail.setIsavailable(Integer.parseInt(String.valueOf(item[9])));
									propertiesDetail.setPhoneNumber((String) item[10]);
									propertiesDetail.setFrontImage((String) item[11]);
									propertiesDetail.setLatitude((String) item[12]);
									propertiesDetail.setLongitude((String) item[13]);
									propertiesDetail.setPrice(Integer.parseInt(String.valueOf(item[16])));
									propertiesDetail.setParking((String) item[17]);
									propertiesDetail.setLocality((String) item[18]);
									propertiesDetails.add(propertiesDetail);
								});

					} else if (request.get("priceRange").contains("<")) {
						String priceRange = request.get("priceRange").substring(1);
						propertiesDetailsRepository.mainPropertiesLessPriceOnly(request.get("latitude"),
								request.get("longitude"), priceRange).forEach(item -> {
									PropertiesDetails propertiesDetail = new PropertiesDetails();
									propertiesDetail.setId((Integer) item[0]);
									propertiesDetail.setName((String) item[1]);
									propertiesDetail.setAddress((String) item[2]);
									propertiesDetail.setBedroom((String) item[3]);
									propertiesDetail.setWashroom((String) item[4]);
									propertiesDetail.setGarage((String) item[5]);
									propertiesDetail.setDescription((String) item[6]);
									propertiesDetail.setArea(Integer.parseInt(String.valueOf(item[7])));
									propertiesDetail.setOwnerEmail((String) item[8]);
									propertiesDetail.setIsavailable(Integer.parseInt(String.valueOf(item[9])));
									propertiesDetail.setPhoneNumber((String) item[10]);
									propertiesDetail.setFrontImage((String) item[11]);
									propertiesDetail.setLatitude((String) item[12]);
									propertiesDetail.setLongitude((String) item[13]);
									propertiesDetail.setPrice(Integer.parseInt(String.valueOf(item[16])));
									propertiesDetail.setParking((String) item[17]);
									propertiesDetail.setLocality((String) item[18]);
									propertiesDetails.add(propertiesDetail);
								});

					} else {

						String[] priceRange = request.get("priceRange").split(",");
						propertiesDetailsRepository.mainPropertiesRangePriceOnly(request.get("latitude"),
								request.get("longitude"), priceRange[0], priceRange[1]).forEach(item -> {

									PropertiesDetails propertiesDetail = new PropertiesDetails();
									propertiesDetail.setId((Integer) item[0]);
									propertiesDetail.setName((String) item[1]);
									propertiesDetail.setAddress((String) item[2]);
									propertiesDetail.setBedroom((String) item[3]);
									propertiesDetail.setWashroom((String) item[4]);
									propertiesDetail.setGarage((String) item[5]);
									propertiesDetail.setDescription((String) item[6]);
									propertiesDetail.setArea(Integer.parseInt(String.valueOf(item[7])));
									propertiesDetail.setOwnerEmail((String) item[8]);
									propertiesDetail.setIsavailable(Integer.parseInt(String.valueOf(item[9])));
									propertiesDetail.setPhoneNumber((String) item[10]);
									propertiesDetail.setFrontImage((String) item[11]);
									propertiesDetail.setLatitude((String) item[12]);
									propertiesDetail.setLongitude((String) item[13]);
									propertiesDetail.setPrice(Integer.parseInt(String.valueOf(item[16])));
									propertiesDetail.setParking((String) item[17]);
									propertiesDetail.setLocality((String) item[18]);
									propertiesDetails.add(propertiesDetail);
								});

					}
				}
			}
		}

		List<Map<String, Object>> propList = new ArrayList<>();
		System.out.println(propertiesDetails.size());
		if (propertiesDetails.size() != 0) {
			propertiesDetails.forEach((item) -> {
				List<Images> images = imagesRepository.getImageByPropertyId(item.getId());
				System.out.println(item);
				Map<String, Object> prop = new HashMap<>();
				prop.put("id", item.getId());
				prop.put("name", item.getName());
				prop.put("address", item.getAddress());
				prop.put("bedroom", item.getBedroom());
				prop.put("washroom", item.getWashroom());
				prop.put("garage", item.getGarage());
				prop.put("description", item.getDescription());
				prop.put("area", item.getArea());
				prop.put("ownerEmail", item.getOwnerEmail());
				prop.put("isavailable", item.getIsavailable());
				prop.put("phoneNumber", item.getPhoneNumber().replaceAll(".(?=.{4})", "*"));
				prop.put("frontImage", item.getFrontImage());
				prop.put("latitude", item.getLatitude());
				prop.put("longitude", item.getLongitude());
				prop.put("property_type", item.getPropertyType());
				prop.put("allowed", item.getAllowed());
				prop.put("furnish", item.getFurnish());
				prop.put("dateCreated", item.getDateCreated());
				prop.put("DateModified", item.getDateModified());
				prop.put("price", item.getPrice());
				prop.put("maintainance", item.getMaintainance());
				prop.put("security", item.getSecurity());
				prop.put("parking", item.getParking());
				prop.put("locality", item.getLocality());
				prop.put("images", images);
				propList.add(prop);
			});
		}
		//
		return propList;
	}

	public Map<String, String> scheduleAppointment(Map<String, String> request, String cidLocation) {
		Map<String, String> response = new HashMap<String, String>();
		if (request.get("images") == null || "".equals(request.get("images"))) {
			response.put("status", "false");
			response.put("message", "Please upload Document proof as well");
			return response;
		}
		if (request.get("email") == null || "".equals(request.get("email"))) {
			response.put("status", "false");
			response.put("message", "Please login to schedule an appointment");
			return response;
		}
		if (request.get("docType") == null || "".equals(request.get("docType"))) {
			response.put("status", "false");
			response.put("message", "Please select document type");
			return response;
		}
		if (request.get("empType") == null || "".equals(request.get("empType"))) {
			response.put("status", "false");
			response.put("message", "Please select employment type");
			return response;
		}
		if (request.get("scheduledDate") == null || "".equals(request.get("scheduledDate"))) {
			response.put("status", "false");
			response.put("message", "Please select date for appointment");
			return response;
		}
		if (request.get("scheduleTime") == null || "".equals(request.get("scheduleTime"))) {
			response.put("status", "false");
			response.put("message", "Please select time for appointment");
			return response;
		}
		String images = (String) request.get("images");
		String email = (String) request.get("email");
		String propertyId = request.get("propertyId");
		Interested interested = interestedRepository.findByEmailAndPropety(email, request.get("propertyId"));
		Users users = usersRepository.findByEmail(email);
		String[] fileData = images.split(",");
		System.out.println(fileData[0]);
		if (users != null) {
			System.out.println(users.getFullName());
			String filename = users.getFullName().replaceAll("[^a-zA-Z0-9]", "") + "_" + propertyId + "_prop" + ".jpeg";
			File file = new File(cidLocation + filename);

			try (FileOutputStream fos = new FileOutputStream(file);) {
				byte[] decoder = Base64.getDecoder().decode(fileData[1]);

				fos.write(decoder);
				System.out.println(cidLocation + filename + "image File Saved");
			} catch (Exception e) {
				e.printStackTrace();
			}
			if (interested != null) {
				interested.setFilename(filename);
				interested.setEmpProof(request.get("docType"));
				interested.setEmpType(request.get("empType"));
				interested.setAppointment(request.get("scheduledDate") + "  " + request.get("scheduleTime"));
				interestedRepository.save(interested);

				response.put("status", "true");
				response.put("message", "Your request successfully registered.Our team will contact you soon");
			} else {
				Interested interestedModel = new Interested();
				interestedModel.setFilename(filename);
				interestedModel.setEmpProof(request.get("docType"));
				interestedModel.setEmpType(request.get("empType"));
				interestedModel.setFullName(users.getFullName());
				interestedModel.setAppointment(request.get("scheduledDate") + "  " + request.get("scheduleTime"));
				interestedModel.setPropertyId(request.get("propertyId"));
				interestedModel.setStatus("Interested");
				interestedModel.setEmail(users.getEmail());
				interestedModel.setFullName(users.getFullName());
				interestedModel.setPhoneNumber(users.getPhoneNumber());
				interestedRepository.save(interestedModel);
				response.put("status", "true");
				response.put("message",
						"your request successfully registered.Our team will contact you soon");
			}
		} else {
			response.put("status", "false");
			response.put("message", "No user found");
		}
		return response;
	}

	public void updateProperty(Map<String, Object> request) {
		PropertiesDetails propertiesDetails = propertiesDetailsRepository.findById((Integer) request.get("id")).get();
		if (propertiesDetails != null) {
			propertiesDetails.setAddress((String) request.get("address"));
			propertiesDetails.setBedroom((String) request.get("bedroom"));
			propertiesDetails.setArea(Integer.parseInt(String.valueOf(request.get("area"))));
			propertiesDetails.setPrice(Integer.parseInt(String.valueOf(request.get("price"))));
			propertiesDetails.setFurnish((String) request.get("furnish"));
			propertiesDetails.setCity((String) request.get("city"));
			propertiesDetails.setState((String) request.get("state"));
			propertiesDetails.setCountry((String) request.get("country"));
			propertiesDetails.setLocality((String) request.get("locality"));
			propertiesDetails.setDescription((String) request.get("description"));
			// propertiesDetails.setFrontImage(frontImage);
			propertiesDetails.setGarage((String) request.get("garage"));
			propertiesDetails.setModular((String) request.get("modular"));
			propertiesDetails.setIsavailable(1);
			propertiesDetails.setLatitude(String.valueOf(request.get("latitude")));
			propertiesDetails.setPropertyType((String) request.get("property_type"));
			propertiesDetails.setAllowed((String) request.get("selectedItems"));
			propertiesDetails.setAmenities((String) request.get("selectedItemsAmenities"));
			propertiesDetails.setLongitude(String.valueOf(request.get("longitude")));
			propertiesDetails.setName((String) request.get("name"));
			propertiesDetails.setPhoneNumber((String) request.get("phoneNumber"));
			propertiesDetails.setWashroom((String) request.get("washroom"));
			propertiesDetails.setMaintainance((String) request.get("maintainance"));
			propertiesDetails.setSecurity((String) request.get("security"));
			propertiesDetailsRepository.save(propertiesDetails);

		}
		// propertiesService.addProperties(propertiesDetails);
	}

	public void updateImages(Map<String, Object> request, String projectLocation) {
		List<Map<String, String>> images = (List<Map<String, String>>) request.get("images");
		String ownerEmail = (String) request.get("ownerEmail");
		String propertyId = (String) request.get("propertyId");
		List<Images> imageList = imagesRepository
				.getImageByPropertyId(Integer.parseInt((String) request.get("propertyId")));
		imageList.forEach(items -> {
			imagesRepository.deleteById(items.getId());
			File file = new File(projectLocation + items.getImageName());
			if (file.delete()) {
				System.out.println("File deleted successfully");
			} else {
				System.out.println("Failed to delete the file");
			}
		});
		Users userDetails=usersRepository.findByEmail(ownerEmail);
		List<Images> imgList = new ArrayList<>();
		images.forEach(item -> {
			Images img = new Images();
			String[] fileData = item.get("fileSource").split(",");
			String filename = userDetails.getFullName().replaceAll("[^a-zA-Z0-9]", "") + "_" + propertyId + "_prop"
					+ String.valueOf(item.get("index")) + ".jpeg";
			File file = new File(projectLocation + filename);
			// images.add(filename);
			img.setImageName(filename);
			// if (i == 0) {
			// img.setIsBanner(1);
			// }
			img.setPropertyId(Integer.parseInt(propertyId));
			imgList.add(img);
			try (FileOutputStream fos = new FileOutputStream(file);) {
				byte[] decoder = Base64.getDecoder().decode(fileData[1]);

				fos.write(decoder);
				System.out.println("image File Saved");
			} catch (Exception e) {
				e.printStackTrace();
			}
		});
		imagesRepository.saveAll(imgList);
	}

	public Map<String, String> afterPayment(Map<String, Object> request) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		System.out.println(request);
		// Subscription
		// subs=subscriptionRepository.getPaymentByTrxnId(request.get("trxnId"));
		// if(subs!=null) {
		// subs.setPaymentId(request.get("paymentId"));
		// subs.setStatus(request.get("status"));
		// }
		return response;
	}

	public Map<String, String> updatePropertyAvailability(Map<String, String> request) {
		Map<String, String> response = new HashMap<>();

		PropertiesDetails propertiesDetails = propertiesDetailsRepository
				.findById(Integer.parseInt(request.get("propertyId"))).get();
		if (propertiesDetails != null) {
			propertiesDetails.setIsApproved(Integer.parseInt(request.get("isApproved")));
			propertiesDetailsRepository.save(propertiesDetails);
			response.put("status", "true");
			response.put("message", "Successfully updated record");
		} else {
			response.put("status", "false");
			response.put("message", "No such property found");
		}
		return response;
	}

	public Map<String, String> beforePayment(Map<String, String> request) {
		System.out.println(request);
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		Subscription subs = new Subscription();
		subs.setPropertyId(Integer.parseInt(request.get("propertyId")));
		subs.setUsername(request.get("username"));
		subs.setTrxnId(request.get("trxnId"));
		subs.setType(request.get("type"));
		subscriptionRepository.save(subs);
		return response;
	}

	public List<Map<String, Object>> filter(Map<String, String> request) {
		List<PropertiesDetails> propertiesDetails = new ArrayList<>();
		int minPrice = 0;
		int maxPrice = 300000;
		int minArea = 0;
		int maxArea = 100000;
		String address = request.get("address");
		String preference = null;
		String propertyType = request.get("propertyType");
		String furnishType = request.get("furnishType");

		List<String> propertyListBHK = new ArrayList<>();
		if (request.get("propertyBHK") != "" || request.get("propertyBHK") != null) {
			if (request.get("propertyBHK") != "5bhk") {
				propertyListBHK.add(request.get("propertyBHK"));
			} else {
				propertyListBHK.add("5BHK");
				propertyListBHK.add("6BHK");
				propertyListBHK.add("7BHK");
				propertyListBHK.add("8BHK");
				propertyListBHK.add("9BHK");
				propertyListBHK.add("10BHK");
				propertyListBHK.add("11BHK");
				propertyListBHK.add("12BHK");
			}
		}
		System.out.println(request.get("minPrice"));
		if (request.get("minPrice") != null) {
			minPrice = Integer.parseInt(request.get("minPrice"));
		}
		if (request.get("maxPrice") != null) {
			maxPrice = Integer.parseInt(request.get("maxPrice"));
		}
		if (request.get("minArea") != null) {
			minArea = Integer.parseInt(request.get("minArea"));

		}
		if (request.get("maxArea") != null) {
			maxArea = Integer.parseInt(request.get("maxArea"));

		}

		if (request.get("preference") != null) {
			preference = "%" + request.get("preference") + "%";
		}
		if ("".equals(request.get("preference"))) {
			preference = null;
		}
		if ("".equals(furnishType)) {
			furnishType = null;
		}
		if ("".equals(propertyType)) {
			propertyType = null;
		}

		if ("".equals(address)) {
			address = null;
		}

		if (request.get("preference") != "" || request.get("preference") != null) {
			propertiesDetails = propertiesDetailsRepository.findFilterWithPreference(furnishType, maxArea, minArea,
					maxPrice, minPrice, preference, propertyListBHK, propertyType, address);
		} else {
			propertiesDetails = propertiesDetailsRepository.findFilter(furnishType,
					Integer.parseInt(request.get("maxArea")), Integer.parseInt(request.get("minArea")),
					Integer.parseInt(request.get("maxPrice")), Integer.parseInt(request.get("minPrice")),
					propertyListBHK, propertyType, address);
		}

		List<Map<String, Object>> propList = new ArrayList<>();
		System.out.println(propertiesDetails.size());
		if (propertiesDetails.size() != 0) {
			propertiesDetails.forEach((item) -> {
				List<Images> images = imagesRepository.getImageByPropertyId(item.getId());
				System.out.println(item);
				Map<String, Object> prop = new HashMap<>();
				prop.put("id", item.getId());
				prop.put("name", item.getName());
				prop.put("address", item.getAddress());
				prop.put("bedroom", item.getBedroom());
				prop.put("washroom", item.getWashroom());
				prop.put("garage", item.getGarage());
				prop.put("description", item.getDescription());
				prop.put("area", item.getArea());
				prop.put("ownerEmail", item.getOwnerEmail());
				prop.put("isavailable", item.getIsavailable());
				prop.put("phoneNumber", item.getPhoneNumber().replaceAll(".(?=.{4})", "*"));
				prop.put("frontImage", item.getFrontImage());
				prop.put("latitude", item.getLatitude());
				prop.put("longitude", item.getLongitude());
				prop.put("property_type", item.getPropertyType());
				prop.put("allowed", item.getAllowed());
				prop.put("furnish", item.getFurnish());
				prop.put("dateCreated", item.getDateCreated());
				prop.put("DateModified", item.getDateModified());
				prop.put("price", item.getPrice());
				prop.put("maintainance", item.getMaintainance());
				prop.put("security", item.getSecurity());
				prop.put("parking", item.getParking());
				prop.put("locality", item.getLocality());
				prop.put("images", images);
				propList.add(prop);
			});
		}
		return propList;

	}

	public List<PropertiesDetails> searchProperties(Map<String, String> request) {
		String condition = "";
		String bathroom = request.get("bathroom") == null ? null : "washroom=" + request.get("bathroom");
		String bedroom = request.get("bedroom") == null ? null : "bedroom=" + request.get("bedroom");
		String garage = request.get("garage") == null ? null : "garage=" + request.get("garage");
		String price = request.get("price") == null ? null : "price<=" + request.get("price");
		String size = request.get("size") == null ? null : "size<=" + request.get("size");
		String furnish = request.get("furnish") == null ? null : "furnish=" + request.get("furnish");
		String address = request.get("address") == null ? null : "furnish=" + request.get("address");
		if (furnish == null) {
			if (bathroom == null) {
				if (bedroom == null) {
					if (garage == null) {
						if (price == null) {
							if (size == null) {

							} else {
								condition = condition + size;
							}
						} else {
							if (size == null) {
								condition = condition + price;
							} else {
								condition = condition + price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + garage;
							} else {
								condition = condition + garage + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + garage + " and " + price;
							} else {
								condition = condition + garage + " and " + price + " and " + size;
							}
						}
					}
				} else {
					if (garage == null) {
						if (price == null) {
							if (size == null) {
								condition = condition + bedroom;
							} else {
								condition = condition + bedroom + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + bedroom + " and " + price;
							} else {
								condition = condition + bedroom + " and " + price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + bedroom + " and " + garage;
							} else {
								condition = condition + bedroom + " and " + garage + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + bedroom + " and " + garage + " and " + price;
							} else {
								condition = condition + bedroom + " and " + garage + " and " + price + " and " + size;
							}
						}
					}
				}
			} else {

				if (bedroom == null) {
					if (garage == null) {
						if (price == null) {
							if (size == null) {
								condition = condition + bathroom;
							} else {
								condition = condition + bathroom + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + bathroom + " and " + price;
							} else {
								condition = condition + bathroom + " and " + price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + bathroom + " and " + garage;
							} else {
								condition = condition + bathroom + " and " + garage + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + bathroom + " and " + garage + " and " + price;
							} else {
								condition = condition + bathroom + " and " + garage + " and " + price + " and " + size;
							}
						}
					}
				} else {
					if (garage == null) {
						if (price == null) {
							if (size == null) {
								condition = condition + bathroom + " and " + bedroom;
							} else {
								condition = condition + bathroom + " and " + bedroom + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + bathroom + " and " + bedroom + " and " + price;
							} else {
								condition = condition + bathroom + " and " + bedroom + " and " + price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + bedroom + " and " + bedroom + " and " + garage;
							} else {
								condition = condition + bedroom + " and " + bedroom + " and " + garage + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + bathroom + " and " + bedroom + " and " + garage + " and "
										+ price;
							} else {
								condition = condition + bathroom + " and " + bedroom + " and " + garage + " and "
										+ price + " and " + size;
							}
						}
					}
				}
			}
		} else {
			if (bathroom == null) {
				if (bedroom == null) {
					if (garage == null) {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish;
							} else {
								condition = condition + furnish + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + price;
							} else {
								condition = condition + furnish + " and " + price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish + " and " + garage;
							} else {
								condition = condition + furnish + " and " + garage + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + garage + " and " + price;
							} else {
								condition = condition + furnish + " and " + garage + " and " + price + " and " + size;
							}
						}
					}
				} else {
					if (garage == null) {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish + " and " + bedroom;
							} else {
								condition = condition + furnish + " and " + bedroom + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + bedroom + " and " + price;
							} else {
								condition = condition + furnish + " and " + bedroom + " and " + price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish + " and " + bedroom + " and " + garage;
							} else {
								condition = condition + furnish + " and " + bedroom + " and " + garage + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + bedroom + " and " + garage + " and "
										+ price;
							} else {
								condition = condition + furnish + " and " + bedroom + " and " + garage + " and " + price
										+ " and " + size;
							}
						}
					}
				}
			} else {

				if (bedroom == null) {
					if (garage == null) {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish + " and " + bathroom;
							} else {
								condition = condition + furnish + " and " + bathroom + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + bathroom + " and " + price;
							} else {
								condition = condition + furnish + " and " + bathroom + " and " + price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish + " and " + bathroom + " and " + garage;
							} else {
								condition = condition + furnish + " and " + bathroom + " and " + garage + " and "
										+ size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + bathroom + " and " + garage + " and "
										+ price;
							} else {
								condition = condition + furnish + " and " + bathroom + " and " + garage + " and "
										+ price + " and " + size;
							}
						}
					}
				} else {
					if (garage == null) {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish + " and " + bathroom + " and " + bedroom;
							} else {
								condition = condition + furnish + " and " + bathroom + " and " + bedroom + " and "
										+ size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + bathroom + " and " + bedroom + " and "
										+ price;
							} else {
								condition = condition + furnish + " and " + bathroom + " and " + bedroom + " and "
										+ price + " and " + size;
							}
						}
					} else {
						if (price == null) {
							if (size == null) {
								condition = condition + furnish + " and " + bedroom + " and " + bedroom + " and "
										+ garage;
							} else {
								condition = condition + furnish + " and " + bedroom + " and " + bedroom + " and "
										+ garage + " and " + size;
							}
						} else {
							if (size == null) {
								condition = condition + furnish + " and " + bathroom + " and " + bedroom + " and "
										+ garage + " and " + price;
							} else {
								condition = condition + furnish + " and " + bathroom + " and " + bedroom + " and "
										+ garage + " and " + price + " and " + size;
							}
						}
					}
				}
			}
		}

		System.out.println(condition);
		// TypedQuery<PropertiesDetails> query = em.createQuery("SELECT * FROM
		// properties_details WHERE "+condition, PropertiesDetails.class);
		// PropertiesDetails pd= query.getSingleResult();
		// System.out.println("#######################@@@@@@@@@@@@@@@@@@@@@@@##########");
		// System.out.println(pd.getLatitude());
		// PropertiesDetails pd = new PropertiesDetails();
		// pd.b
		// if(address==null) {
		return propertiesDetailsRepository.searchProperties("SELECT * FROM properties_details where " + condition);
		// }else {
		// if(address==null) {
		// propertiesDetailsRepository.searchProperties(condition,);
		// }
		// }

		// List<PropertiesDetails> response = new ArrayList();
		// return response;
	}

	public void matchRequirements(Map<String, String> request) {
		String email = request.get("email");
		MatchingRequirements matchingRequirements = matchingRequirementsRepository.findByEmail(email);
		if (matchingRequirements != null) {
			matchingRequirements.setBhk(request.get("propertyBHK"));
			matchingRequirements.setAreaRange(request.get("minArea")+"-"+request.get("maxArea"));
			matchingRequirements.setPriceRange(request.get("minPrice")+"-"+request.get("maxPrice"));
			matchingRequirements.setFurnish(request.get("furnishType"));
			matchingRequirements.setCity(request.get("city"));
			matchingRequirements.setState(request.get("state"));
			matchingRequirements.setLocality(request.get("locality"));
			matchingRequirements.setPreference(request.get("preference"));
			matchingRequirements.setPropertyType(request.get("propertyType"));
			matchingRequirements.setEmail(email);
		} else {
			matchingRequirements = new MatchingRequirements();
			matchingRequirements.setBhk(request.get("propertyBHK"));
			matchingRequirements.setAreaRange(request.get("minArea")+"-"+request.get("maxArea"));
			matchingRequirements.setPriceRange(request.get("minPrice")+"-"+request.get("maxPrice"));
			matchingRequirements.setFurnish(request.get("furnishType"));
			matchingRequirements.setCity(request.get("city"));
			matchingRequirements.setState(request.get("state"));
			matchingRequirements.setLocality(request.get("locality"));
			matchingRequirements.setPreference(request.get("preference"));
			matchingRequirements.setPropertyType(request.get("propertyType"));
			matchingRequirements.setEmail(email);
		}
		matchingRequirementsRepository.save(matchingRequirements);
	}

	public Map<String, String> addProperties(Map<String, Object> request, String projectLocation) {
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		response.put("message", "Successfully added property");
		ArrayList<String> fileSource = request.get("fileSource") == "" ? new ArrayList<>()
				: (ArrayList<String>) request.get("fileSource");
		ArrayList<Map<String, String>> allowed = (ArrayList<Map<String, String>>) request.get("selectedItems");
		ArrayList<Map<String, String>> amenities = (ArrayList<Map<String, String>>) request
				.get("selectedItemsAmenities");
		String allowedString = "";
		for (Map<String, String> value : allowed) {
			if (allowedString != "") {
				allowedString = allowedString + "," + value.get("item_id");
			} else {
				allowedString = allowedString + value.get("item_id");
			}
		}
		String amenitiesString = "";
		for (Map<String, String> value : amenities) {
			if (amenitiesString != "") {
				amenitiesString = amenitiesString + "," + value.get("item_id");
			} else {
				amenitiesString = amenitiesString + value.get("item_id");
			}
		}
		String ownerEmail = request.get("ownerEmail") == null || request.get("ownerEmail") == "" ? ""
				: (String) request.get("ownerEmail");
		String locality = request.get("locality") == null || request.get("locality") == "" ? ""
				: (String) request.get("locality");
		String city = request.get("city") == null || request.get("city") == "" ? "" : (String) request.get("city");
		String state = request.get("state") == null || request.get("state") == "" ? "" : (String) request.get("state");
		String price = request.get("price") == null || request.get("price") == "" ? "" : (String) request.get("price");
		String area = request.get("area") == null || request.get("area") == "" ? "" : (String) request.get("area");
		String address = request.get("address") == null || request.get("address") == "" ? ""
				: (String) request.get("address");
		String bedroom = request.get("bedroom") == null || request.get("bedroom") == "" ? ""
				: (String) request.get("bedroom");
		String furnish = request.get("furnish") == null || request.get("furnish") == "" ? ""
				: (String) request.get("furnish");
		String country = request.get("country") == null || request.get("country") == "" ? ""
				: (String) request.get("country");
		String description = request.get("description") == null || request.get("description") == "" ? ""
				: (String) request.get("description");
		String modular = request.get("modular") == null || request.get("modular") == "" ? ""
				: (String) request.get("modular");
		String parking = request.get("parking") == null || request.get("parking") == "" ? ""
				: (String) request.get("parking");
		String garage = request.get("garage") == null || request.get("garage") == "" ? ""
				: (String) request.get("garage");
		String latitude = request.get("latitude") == null || request.get("latitude") == "" ? ""
				: String.valueOf(request.get("latitude"));
		String propertyType = request.get("propertyType") == null || request.get("propertyType") == "" ? ""
				: (String) request.get("propertyType");
		String longitude = request.get("longitude") == null || request.get("longitude") == "" ? ""
				: String.valueOf(request.get("longitude"));
		String name = request.get("name") == null || request.get("name") == "" ? "" : (String) request.get("name");
		String phoneNumber = request.get("phoneNumber") == null || request.get("phoneNumber") == "" ? ""
				: (String) request.get("phoneNumber");
		String washrooms = request.get("washrooms") == null || request.get("washrooms") == "" ? ""
				: (String) request.get("washrooms");
		String maintainance = request.get("maintainance") == null || request.get("maintainance") == "" ? ""
				: (String) request.get("maintainance");
		String security = request.get("security") == null || request.get("security") == "" ? ""
				: (String) request.get("security");
		String addprf = request.get("addressProof") == null || request.get("addressProof") == "" ? ""
				: (String) request.get("addressProof");
		System.out.println(fileSource.isEmpty() + "price###############" + price.equals(""));
		System.out.println(fileSource);
		System.out.println(addprf);
		System.out.println(price);
		if (fileSource.isEmpty()) {
			System.out.println("#################FILESOURCE");
			response.put("status", "false");
			response.put("message", "Please upload add least one property image");
			return response;
		}

		if (price.equals("")) {
			response.put("status", "false");
			response.put("message", "Please Enter price");
			return response;
		}
		if (area.equals("")) {
			response.put("status", "false");
			response.put("message", "Please enter area");
			return response;
		}
		if (ownerEmail.equals("")) {
			response.put("status", "false");
			response.put("message", "Please login to add property");
			return response;
		}
		if (locality.equals("")) {
			response.put("status", "false");
			response.put("message", "Please enter locality");
			return response;
		}
		if (city.equals("")) {
			response.put("status", "false");
			response.put("message", "Please enter city");
			return response;
		}
		if (state.equals("")) {
			response.put("status", "false");
			response.put("message", "Please enter state");
			return response;
		}
		ArrayList<String> images = new ArrayList<>();
		PropertiesDetails propertiesDetails = new PropertiesDetails();
		propertiesDetails.setAddress(address);
		propertiesDetails.setBedroom(bedroom);
		propertiesDetails.setArea(area.equals("") ? 0 : Integer.parseInt(area));
		propertiesDetails.setPrice(price.equals("") ? 0 : Integer.parseInt(price));
		propertiesDetails.setFurnish(furnish);
		propertiesDetails.setCity(city);
		propertiesDetails.setState(state);
		propertiesDetails.setCountry(country);
		propertiesDetails.setLocality(locality);
		propertiesDetails.setDescription(description);
		propertiesDetails.setModular(modular);
		propertiesDetails.setParking(parking);
		propertiesDetails.setGarage(garage);
		propertiesDetails.setIsavailable(1);
		propertiesDetails.setLatitude(latitude);
		propertiesDetails.setPropertyType(propertyType);
		propertiesDetails.setAllowed(allowedString);
		propertiesDetails.setAmenities(amenitiesString);
		propertiesDetails.setLongitude(longitude);
		propertiesDetails.setName(name);
		propertiesDetails.setOwnerEmail(ownerEmail);
		propertiesDetails.setPhoneNumber(phoneNumber);
		propertiesDetails.setWashroom(washrooms);
		propertiesDetails.setMaintainance(maintainance);
		propertiesDetails.setSecurity(security);
		PropertiesDetails propertiesDetailsResponse = propertiesDetailsRepository.save(propertiesDetails);
		Users userDetails= usersRepository.findByEmail(ownerEmail);
		if(userDetails==null) {
			response.put("status", "false");
			response.put("message", "Please login");
			return response;
		}
		List<Images> imgList = new ArrayList<>();
		for (int i = 0; i < fileSource.size(); i++) {
			Images img = new Images();
			String[] fileData = fileSource.get(i).split(",");
			String filename = userDetails.getFullName() + "_" + propertiesDetailsResponse.getId() + "_prop" + i + ".jpeg";
			File file = new File(projectLocation + filename);
			images.add(filename);
			img.setImageName(filename);
			if (i == 0) {
				img.setIsBanner(1);
			}
			img.setPropertyId(propertiesDetailsResponse.getId());
			imgList.add(img);
			try (FileOutputStream fos = new FileOutputStream(file);) {
				if (fileData[1] != null) {
					byte[] decoder = Base64.getDecoder().decode(fileData[1]);
					fos.write(decoder);
				} else {
					response.put("status", "false");
					response.put("message", "Not a correct file uploaded");
					return response;
				}
			} catch (Exception e) {
				e.printStackTrace();
				response.put("status", "false");
				response.put("message", "File path not correct");
				return response;
			}

		}
		if (!addprf.equals("")) {
		String addressProoff = "AddressProof_" + propertiesDetailsResponse.getId() + ".jpeg";
		File addProof = new File(projectLocation + addressProoff);
		String[] addprfArray = addprf.split(",");
		try (FileOutputStream fos1 = new FileOutputStream(addProof);) {
			if (addprfArray[1] != null) {
				byte[] decoder1 = Base64.getDecoder().decode(addprfArray[1]);
				fos1.write(decoder1);
			} else {
				response.put("status", "false");
				response.put("message", "Not a correct address proof uploaded");
				return response;
			}

			System.out.println("address File Saved");
		} catch (Exception e) {
			e.printStackTrace();
			response.put("status", "false");
			response.put("message", "File path not correct");
			return response;
		}
		propertiesDetailsResponse.setAddressProof(addressProoff);
	}
		imagesRepository.saveAll(imgList);
		propertiesDetailsRepository.save(propertiesDetailsResponse);
		return response;
	}

	public List<Map<String, Object>> findPropertiesNearMe(Map<String, String> request) {
		List<Map<String, Object>> propertyList = new ArrayList<>();
		propertiesDetailsRepository.findPropertiesNearMe(request.get("latitude"), request.get("longitude"))
				.forEach((item) -> {
					System.out.println((Integer) item[0]);
					List<Images> images = imagesRepository.getImageByPropertyId((Integer) item[0]);
					System.out.println(item);
					Map<String, Object> prop = new HashMap<>();
					prop.put("id", item[0]);
					prop.put("name", item[1]);
					prop.put("address", item[2]);
					prop.put("bedroom", item[3]);
					prop.put("washroom", item[4]);
					prop.put("garage", item[5]);
					prop.put("description", item[6]);
					prop.put("area", item[7]);
					prop.put("ownerEmail", item[8]);
					prop.put("isavailable", item[9]);
					prop.put("phoneNumber", String.valueOf(item[10]).replaceAll(".(?=.{4})", "*"));
					prop.put("frontImage", item[11]);
					prop.put("latitude", item[12]);
					prop.put("longitude", item[13]);
					prop.put("dateCreated", item[14]);
					prop.put("DateModified", item[15]);
					prop.put("price", item[16]);
					prop.put("distance", item[17]);
					prop.put("images", images);

					propertyList.add(prop);
				});
		return propertyList;
	}

	public Map<String, Object> fetchPropertiesById(int id) {
		// List<Map<String, Object>> propertyList = new ArrayList<>();
		PropertiesDetails item = propertiesDetailsRepository.fetchPropertiesById(id);
		// .forEach((item) -> {
		List<Images> images = imagesRepository.getImageByPropertyId(id);
		System.out.println(item);
		Map<String, Object> prop = new HashMap<>();
		prop.put("id", item.getId());
		prop.put("name", item.getName());
		prop.put("address", item.getAddress());
		prop.put("bedroom", item.getBedroom());
		prop.put("washroom", item.getWashroom());
		prop.put("garage", item.getGarage());
		prop.put("description", item.getDescription());
		prop.put("area", item.getArea());
		prop.put("ownerEmail", item.getOwnerEmail());
		prop.put("isavailable", item.getIsavailable());
		prop.put("phoneNumber", item.getPhoneNumber().replaceAll(".(?=.{4})", "*"));
		prop.put("frontImage", item.getFrontImage());
		prop.put("latitude", item.getLatitude());
		prop.put("longitude", item.getLongitude());
		prop.put("property_type", item.getPropertyType());
		prop.put("allowed", item.getAllowed());
		prop.put("furnish", item.getFurnish());
		prop.put("dateCreated", item.getDateCreated());
		prop.put("DateModified", item.getDateModified());
		prop.put("price", item.getPrice());
		prop.put("maintainance", item.getMaintainance());
		prop.put("security", item.getSecurity());
		prop.put("images", images);
		prop.put("address", item.getAddress());
		prop.put("locality", item.getLocality());
		prop.put("city", item.getCity());
		prop.put("state", item.getState());
		prop.put("amenities", item.getAmenities());
		prop.put("parking", item.getParking());
		prop.put("modular", item.getModular());
		prop.put("country", item.getCountry());

		// propertyList.add(prop);
		// });
		return prop;
	}

}

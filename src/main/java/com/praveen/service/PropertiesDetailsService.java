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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
import com.praveen.dao.PropertiesDetailsRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.model.Images;
import com.praveen.model.Interested;
import com.praveen.model.PropertiesDetails;
import com.praveen.model.Users;

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
	@PersistenceContext
	public EntityManager em;
	
	public List<Map<String, Object>> manageProperties(Map<String, String> respo) {
		String UserName = respo.get("username");
		List<Map<String, Object>> response = new ArrayList<>();
		propertiesDetailsRepository.findByOwnerName(UserName).forEach((item) -> {
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
			prop.put("ownerName", item.getOwnerName());
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
			prop.put("frontImage", images.get(0).getImageName());
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

	public void deleteInterestedProperties(Integer propertyId, String username) {
		interestedRepository.deleteInterestedProperties(propertyId, username);
	}

	public List<Map<String, Object>> sortlistedProperties(String username) {
		System.out.println(username);
		List<Map<String, Object>> response = new ArrayList<>();

		propertiesDetailsRepository.fetchInterestedPropertyByUsername(username).forEach((item) -> {
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
			prop.put("ownerName", item.getOwnerName());
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
			prop.put("frontImage", images.get(0).getImageName());
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
			header2.setCellValue("owner_name");
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
			CreationHelper createHelper = workbook.getCreationHelper();
			for (PropertiesDetails properties : resultLeads) {
				// System.out.println(properties.getFullName());
				// this creates a new row in the sheet
				Row row = sheet.createRow(rownum++);
				int cellnum = 0;
				Cell cell1 = row.createCell(cellnum++);
				cell1.setCellValue(properties.getName());
				Cell cell2 = row.createCell(cellnum++);
				cell2.setCellValue(properties.getOwnerName());
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
		if (request.get("priceRange").contains("<")) {
			propertiesDetails.addAll(propertiesDetailsRepository.mainPropertiesGreaterPrice(request.get("address"),
					request.get("priceRange"), request.get("propertyBhk"), request.get("type")));
		} else if (request.get("priceRange").contains(">")) {
			propertiesDetails.addAll(propertiesDetailsRepository.mainPropertiesLessPrice(request.get("address"),
					request.get("priceRange"), request.get("propertyBhk"), request.get("type")));

		} else {
			System.out.println("between");
			String[] priceRange = request.get("priceRange").split(",");
			System.out.println(request.get("address") + "####" + priceRange[0] + "####" + priceRange[1] + "####"
					+ request.get("propertyBhk") + "####" + request.get("type"));
			System.out.println("SELECT * FROM properties_details WHERE (((city LIKE %" + request.get("address")
					+ "% or locality LIKE %" + request.get("address") + "%) and price between " + priceRange[0]
					+ " and " + priceRange[1] + ") and property_type=" + request.get("type") + ") and name="
					+ request.get("propertyBhk"));
			propertiesDetails
					.addAll(propertiesDetailsRepository.mainPropertiesRangePrice("%" + request.get("address") + "%",
							priceRange[0], priceRange[1], request.get("propertyBhk"), request.get("type")));

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
				prop.put("ownerName", item.getOwnerName());
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
				propList.add(prop);
			});
		}
		//
		return propList;
	}

	public void scheduleAppointment(Map<String,String> request,String cidLocation) {
		String images = (String) request.get("images");
		String username = (String) request.get("username");
		String propertyId = request.get("propertyId");
		Interested interested= interestedRepository.findByUsernameAndPropety(username, request.get("propertyId"));
		if(interested!=null) {
		interested.setAppointment(request.get("scheduledDate")+"  "+ request.get("scheduleTime"));
		interestedRepository.save(interested);
		}else {
			String[] fileData = images.split(",");
			System.out.println(fileData[0]);
			String filename = username.replaceAll("[^a-zA-Z0-9]", "") + "_" + propertyId + "_prop" + ".jpeg";
			File file = new File(cidLocation + filename);
			
			try (FileOutputStream fos = new FileOutputStream(file);) {
				byte[] decoder = Base64.getDecoder().decode(fileData[1]);

				fos.write(decoder);
				System.out.println("image File Saved");
			} catch (Exception e) {
				e.printStackTrace();
			}
			Users user=usersRepository.findByUsername(request.get("username"));
			Interested interestedModel= new Interested();
			interestedModel.setFilename(filename);
			interestedModel.setUsername(request.get("username"));
			interestedModel.setAppointment(request.get("scheduledDate")+"  "+ request.get("scheduleTime"));
			interestedModel.setPropertyId(request.get("propertyId"));
			interestedModel.setStatus("Interested");
			interestedModel.setEmail(user.getEmail());
			interestedModel.setFullName(user.getFullName());
			interestedModel.setPhoneNumber(user.getPhoneNumber());
			interestedRepository.save(interestedModel);
		}
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
			propertiesDetails.setIsavailable(1);
			propertiesDetails.setLatitude(String.valueOf(request.get("latitude")));
			propertiesDetails.setPropertyType((String) request.get("property_type"));
			propertiesDetails.setAllowed((String) request.get("allowed"));
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
		String ownerName = (String) request.get("ownerName");
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
		List<Images> imgList = new ArrayList<>();
		images.forEach(item -> {
			Images img = new Images();
			String[] fileData = item.get("fileSource").split(",");
			String filename = ownerName.replaceAll("[^a-zA-Z0-9]", "") + "_" + propertyId + "_prop"
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
//		TypedQuery<PropertiesDetails> query = em.createQuery("SELECT * FROM properties_details WHERE "+condition, PropertiesDetails.class);
//		PropertiesDetails pd= query.getSingleResult();
//		System.out.println("#######################@@@@@@@@@@@@@@@@@@@@@@@##########");
//		System.out.println(pd.getLatitude());
//		PropertiesDetails pd = new PropertiesDetails();
//		pd.b
		// if(address==null) {
		return propertiesDetailsRepository.searchProperties("SELECT * FROM properties_details where "+condition);
		// }else {
		// if(address==null) {
		// propertiesDetailsRepository.searchProperties(condition,);
		// }
		// }

		// List<PropertiesDetails> response = new ArrayList();
		// return response;
	}

	public void addProperties(Map<String, Object> request, String projectLocation) {
		ArrayList<String> fileSource = (ArrayList<String>) request.get("fileSource");
		System.out.println();
		String ownerName = (String) request.get("ownerName");
		ArrayList<String> images = new ArrayList<>();
		// "D:\\proj\\realestate\\vicidialui\\src\\assets\\properties\\" + ownerName +
		// "_prop1.jpeg";

		PropertiesDetails propertiesDetails = new PropertiesDetails();
		propertiesDetails.setAddress((String) request.get("address"));
		propertiesDetails.setBedroom((String) request.get("bedroom"));
		propertiesDetails.setArea(Integer.parseInt((String) request.get("area")));
		propertiesDetails.setPrice(Integer.parseInt((String) request.get("price")));
		propertiesDetails.setFurnish((String) request.get("furnish"));
		propertiesDetails.setCity((String) request.get("city"));
		propertiesDetails.setState((String) request.get("state"));
		propertiesDetails.setCountry((String) request.get("country"));
		propertiesDetails.setLocality((String) request.get("locality"));
		propertiesDetails.setDescription((String) request.get("description"));
		propertiesDetails.setModular((String) request.get("modular"));
		propertiesDetails.setModular((String) request.get("parking"));
		// propertiesDetails.setFrontImage(frontImage);
		propertiesDetails.setGarage((String) request.get("garage"));
		propertiesDetails.setIsavailable(1);
		propertiesDetails.setLatitude(String.valueOf(request.get("latitude")));
		propertiesDetails.setPropertyType((String) request.get("propertyType"));
		propertiesDetails.setAllowed(String.join(",", (ArrayList<String>) request.get("allowed")));
		propertiesDetails.setLongitude(String.valueOf(request.get("longitude")));
		propertiesDetails.setName((String) request.get("name"));
		propertiesDetails.setOwnerName(ownerName);
		propertiesDetails.setPhoneNumber((String) request.get("phoneNumber"));
		propertiesDetails.setWashroom((String) request.get("washrooms"));
		propertiesDetails.setMaintainance((String) request.get("maintainance"));
		propertiesDetails.setSecurity((String) request.get("security"));
		// propertiesService.addProperties(propertiesDetails);
		PropertiesDetails propertiesDetailsResponse = propertiesDetailsRepository.save(propertiesDetails);

		List<Images> imgList = new ArrayList<>();
		for (int i = 0; i < fileSource.size(); i++) {
			Images img = new Images();
			String[] fileData = fileSource.get(i).split(",");
			String filename = ownerName + "_" + propertiesDetailsResponse.getId() + "_prop" + i + ".jpeg";
			File file = new File(projectLocation + filename);
			images.add(filename);
			img.setImageName(filename);
			if (i == 0) {
				img.setIsBanner(1);
			}
			img.setPropertyId(propertiesDetailsResponse.getId());
			imgList.add(img);
			try (FileOutputStream fos = new FileOutputStream(file);) {
				byte[] decoder = Base64.getDecoder().decode(fileData[1]);

				fos.write(decoder);
				System.out.println("image File Saved");
			} catch (Exception e) {
				e.printStackTrace();
			}

		}
		imagesRepository.saveAll(imgList);

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
					prop.put("ownerName", item[8]);
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
		prop.put("ownerName", item.getOwnerName());
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
		prop.put("country", item.getCountry());

		// propertyList.add(prop);
		// });
		return prop;
	}

}

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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

import com.praveen.model.Payments;
import com.praveen.model.PropertiesDetails;
import com.praveen.model.Users;

import org.apache.commons.collections4.MultiMap;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.MediaType;
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
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import com.praveen.dao.PaymentsRepository;
import com.praveen.dao.PropertiesDetailsRepository;
import com.praveen.dao.UsersRepository;
import com.praveen.service.AdminUserService;
import com.praveen.service.EmailServiceImpl;
import com.praveen.service.PropertiesDetailsService;
import com.praveen.service.UsersService;

@Controller
@RequestMapping("/realestate")
public class EngineController {

	@Autowired
	private HttpServletRequest request;
	@Autowired
	UsersService usersService;
	@Autowired
	PaymentsRepository paymentsRepository;
	@Autowired
	PropertiesDetailsService propertiesDetailsService;
	@Autowired
	PropertiesDetailsRepository propertiesDetailsRepository;
	@Autowired
	UsersRepository usersRepository;
	@Autowired
	AdminUserService adminUserService;

	@Autowired
	EmailServiceImpl emailServiceImpl;
	@Value("${project.location}")
	String projectLocation;
	@Value("${cid.location}")
	String cidLocation;
	@Value("${spring.mail.username}")
	String username;

	public enum PaymentMode {

		NB, DC, CC
	}

	@Autowired
	private UsersRepository userRepository;

	@RequestMapping("/")
	public String home(Map<String, Object> model) {
		model.put("message", "HowToDoInJava Reader !!");
		return "index";
	}

	@CrossOrigin
	@PostMapping(path = "/sendemail", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> sendEmail(@RequestBody(required = true) Map<String, String> resp) {
		return emailServiceImpl.sendSimpleMessage(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/resetPassword", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> resetPassword(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.resetPassword(resp);
	}

	@RequestMapping("/failure")
	public String failure(Map<String, Object> model) {
		return "failure";
	}

	@RequestMapping("/success")
	public String success(Map<String, Object> model) {
		return "success";
	}

	@CrossOrigin
	@ResponseBody
	@GetMapping("/searchAddress/{address}")
	public List<String> searchAddress(@PathVariable("address") String address) {
		return propertiesDetailsService.searchAddress(address);
	}

	@CrossOrigin
	@ResponseBody
	@GetMapping("/updatePropertyStatus/{propertyId}/{status}")
	public Map<String, String> updatePropertyStatus(@PathVariable("propertyId") int propertyId,
			@PathVariable("status") int status) {
		propertiesDetailsService.updatePropertyStatus(propertyId, status);
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(path = "/sortlistedProperties", consumes = "application/json", produces = "application/json")
	public List<Map<String, Object>> sortlistedProperties(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.sortlistedProperties(resp.get("email"));
	}

	@CrossOrigin
	@ResponseBody
	@PostMapping(path = "/deleteInterestedProperties", consumes = "application/json", produces = "application/json")
	public Map<String, String> deleteInterestedProperties(@RequestBody(required = true) Map<String, String> resp) {
		propertiesDetailsService.deleteInterestedProperties(Integer.parseInt(resp.get("propertyId")), resp.get("email"));
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@ResponseBody
	@GetMapping("/fetchPropertiesById/{id}")
	public Map<String, Object> fetchPropertiesById(@PathVariable("id") int id) {
		return propertiesDetailsService.fetchPropertiesById(id);
	}
	
	@CrossOrigin
	@ResponseBody
	@GetMapping("/fetchProperties")
	public List<PropertiesDetails> fetchProperties() {
		return propertiesDetailsRepository.findAll();
	}
	
	@CrossOrigin
	@ResponseBody
	@GetMapping("/fetchUnapprovedProperties")
	public List<PropertiesDetails> fetchUnapprovedProperties() {
		return propertiesDetailsRepository.fetchUnapprovedProperties();
	}
	@CrossOrigin
	@ResponseBody
	@GetMapping("/fetchAllTenants")
	public List<Users> fetchAllTenants() {
		return userRepository.findAllTenants();
	}
	
	@CrossOrigin
	@ResponseBody
	@GetMapping("/fetchUsersByPhoneNumberOrEmail/{email}/{phoneNumber}")
	public Users fetchUsersByPhoneNumberOrEmail(@PathVariable("email") String email,@PathVariable("phoneNumber") String phoneNumber) {
		if(!email.isEmpty()) {
			return usersRepository.findByEmail(email);
		}
		
		if(!phoneNumber.isEmpty()) {
			return usersRepository.findByPhoneNumber(phoneNumber);
		}
		return new Users();
		
	}
	@CrossOrigin
	@ResponseBody
	@GetMapping("/fetchTenantsDetailsById/{id}")
	public List<Map<String,String>> fetchTenantsDetailsById(@PathVariable("id") String id) {
		List<Map<String,String>> responseList= new ArrayList<>();
		this.usersRepository.findUserDetailsById(Integer.parseInt(id)).forEach(items->{
			Map<String,String> response= new HashMap<>();
			response.put("username", (String)items[0]);
			response.put("email", (String)items[1]);
			response.put("phone_number", (String)items[2]);
			response.put("property_id", (String)items[3]);
			response.put("status", (String)items[4]);
			response.put("appointment", (String)items[5]);
			response.put("emp_proof", (String)items[6]);
			response.put("emp_type", (String)items[7]);
			response.put("filename", (String)items[8]);
			responseList.add(response);
		});
		return responseList;
		
	}
	@CrossOrigin
	@PostMapping(path = "/searchProperties", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<PropertiesDetails> searchProperties(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.searchProperties(resp);
	}
	@CrossOrigin
	@PostMapping(path = "/filter", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> filter(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.filter(resp);
	}
	
	@CrossOrigin
	@PostMapping(path = "/searchUser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Users searchUser(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.searchUserByEmailOrUsername(resp.get("type"));
	}
	
	@CrossOrigin
	@PostMapping(path = "/updatePropertyAvailability", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String,String> updatePropertyAvailability(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.updatePropertyAvailability(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/beforePayment", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> beforePayment(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.beforePayment(resp);
	}

	// @CrossOrigin
	// @PostMapping(path = "/afterPayment", consumes =
	// "application/x-www-form-urlencoded;charset=UTF-8", produces =
	// "application/json")
	// @ResponseBody
	// public Map<String,String> afterPayment(@RequestBody(required = true)
	// Map<String, Object> resp) {
	// return propertiesDetailsService.afterPayment(resp);
	// }
	@RequestMapping(path = "/afterPayment", method = RequestMethod.POST)
	public String afterPayment(@RequestParam String mihpayid, @RequestParam String status,
			@RequestParam PaymentMode mode, @RequestParam String txnid, @RequestParam String bankcode,
			@RequestParam String PG_TYPE, @RequestParam String bank_ref_num, @RequestParam String amount,
			@RequestParam String addedon, @RequestParam String productinfo, @RequestParam String email,
			@RequestParam String payuMoneyId) {
		Payments paymentExisting = paymentsRepository.findByTxnId(txnid);
		if (paymentExisting == null) {
			String[] trxnArray = txnid.split("_");
			String username = trxnArray[0];
			Payments payment = new Payments();
			payment.setAddedon(addedon);
			payment.setMihpayid(mihpayid);
			payment.setStatus(status);
			payment.setPayuMoneyId("");
			payment.setTxnid(txnid);
			payment.setBankcode(bankcode);
			payment.setPG_TYPE(PG_TYPE);
			payment.setBank_ref_num(bank_ref_num);
			payment.setAmount(amount);
			payment.setProductinfo(productinfo);
			payment.setEmail(email);
			payment.setPayuMoneyId(payuMoneyId);
			payment.setUsername(username);
			paymentsRepository.save(payment);

		}
		Users user = userRepository.findByUsername(username);
		if (status.equals("failure")) {
			Map<String, String> request = new HashMap<>();
			if (user != null) {
				request.put("to", user.getEmail());
				request.put("hostName", "ownertenants.com");
				request.put("type", "paymentFailed");
				request.put("from", username);
				request.put("subject", "Payment Failed");
				request.put("paymentId", payuMoneyId);
				this.emailServiceImpl.sendSimpleMessage(request);
			}
			return "failure";
		} else {
			Map<String, String> request = new HashMap<>();

			if (user != null) {
				request.put("to", user.getEmail());
				request.put("hostName", "ownertenants.com");
				request.put("type", "paymentSuccess");
				request.put("from", username);
				request.put("subject", "Payment successfully completed");
				request.put("paymentId", payuMoneyId);
				this.emailServiceImpl.sendSimpleMessage(request);
			}
			return "success";
		}

	}

	@CrossOrigin
	@PostMapping(path = "/scheduleAppointment", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> scheduleAppointment(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.scheduleAppointment(resp, cidLocation);
//		Map<String, String> response = new HashMap<>();
//		response.put("status", "true");
//		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/updateImages", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateImages(@RequestBody(required = true) Map<String, Object> resp) {
		propertiesDetailsService.updateImages(resp, projectLocation);
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/updateProperty", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> updateProperty(@RequestBody(required = true) Map<String, Object> resp) {
		propertiesDetailsService.updateProperty(resp);
		Map<String, String> response = new HashMap<>();
		response.put("status", "true");
		return response;
	}

	@CrossOrigin
	@PostMapping(path = "/manageProperties", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> manageProperties(@RequestBody(required = true) Map<String, String> resp) {
		System.out.println("######################################");
		return propertiesDetailsService.manageProperties(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/fetchreportdatabetween", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String,String> fetchreportdatabetween(@RequestBody(required = true) Map<String, Object> resp) {
		Map<String,String> response= new HashMap<>();
		response.put("status", "true");
		 usersService.fetchreportdatabetween(resp, projectLocation);
		 return response;
	}

	@CrossOrigin
	@PostMapping(path = "/fetchreportdatabetweenpropertyadded", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String,String>  fetchreportdatabetweenpropertyadded(
			@RequestBody(required = true) Map<String, Object> resp) {
		Map<String,String> response= new HashMap<>();
		response.put("status", "true");
		 propertiesDetailsService.fetchreportdatabetweenpropertyadded(resp, projectLocation);
		 return response;
	}

	@CrossOrigin
	@PostMapping(path = "/mainProperties", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public List<Map<String, Object>> mainProperties(@RequestBody(required = true) Map<String, String> resp) {
		return propertiesDetailsService.mainProperties(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/validateuser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> validateUser(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.validateUser(resp);
	}

	@CrossOrigin
	@PostMapping(path = "/validateUserName", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> validateUserName(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.validateUserName(resp);
	}
	@CrossOrigin
	@PostMapping(path = "/validateEmail", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> validateEmail(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.validateEmail(resp);
	}
	
	@CrossOrigin
	@PostMapping(path = "/addProperties", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> addProperties(@RequestBody(required = true) Map<String, Object> resp) {
		return propertiesDetailsService.addProperties(resp, projectLocation);
	}

	@CrossOrigin
	@PostMapping(path = "/matchRequirements", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> matchRequirements(@RequestBody(required = true) Map<String, String> resp) {
		Map<String, String> response = new HashMap<>();
		propertiesDetailsService.matchRequirements(resp);
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
		return usersService.registerUser(resp);
	}
	@CrossOrigin
	@PostMapping(path = "/validateadminuser", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> validateadminuser(@RequestBody(required = true) Map<String, String> resp) {
		return adminUserService.validateUser(resp);
	}
	
	
	@CrossOrigin
	@PostMapping(path = "/interested", consumes = "application/json", produces = "application/json")
	@ResponseBody
	public Map<String, String> interested(@RequestBody(required = true) Map<String, String> resp) {
		return usersService.interested(resp);

	}

}

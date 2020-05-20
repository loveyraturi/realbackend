package com.praveen.service;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.praveen.dao.CampaingLeadMappingRepository;
import com.praveen.dao.CampaingRepository;
import com.praveen.dao.LeadsRepository;
import com.praveen.model.Campaing;
import com.praveen.model.CampaingLeadMapping;
import com.praveen.model.Leads;
import com.praveen.model.UserGroup;

@Service
public class LeadsService {
@Autowired
LeadsRepository leadRepository;
@Autowired
CampaingRepository campaingRepository;
@Autowired
CampaingLeadMappingRepository campaingLeadMappingRepository;

	public List<Leads> fetchAllLeads(){
		return leadRepository.findAll();
	}
	
	public void createLead(Map<String, String> request) {
		System.out.println(request);
		Leads leads = new Leads();
		leads.setAddress1(request.get("address1"));
		leads.setAddress2(request.get("address2"));
		leads.setAddress3(request.get("address3"));
		leads.setAlternatePhonenumber(request.get("alternatePhonenumber"));
		leads.setCity(request.get("city"));
		leads.setCountryCode(request.get("countryCode"));
		leads.setEmail(request.get("email"));
		leads.setFirstName(request.get("firstName"));
		leads.setGender(request.get("gender"));
		leads.setLastName(request.get("lastName"));
		leads.setMiddleName(request.get("middleName"));
		leads.setPhoneNumber(request.get("phoneNumber"));
		leads.setState(request.get("state"));
		leads.setPostalCode(request.get("postalCode"));
		leads.setStatus(request.get("status"));
		leadRepository.save(leads);
	}
	public void feedback(Map<String,String> request) {
		Leads leads=leadRepository.findById(Integer.parseInt(request.get("id"))).get();
		leads.setComments(request.get("comment"));
		leads.setStatus(request.get("status"));
		leadRepository.save(leads);
	}
	
	public void createLeadWithCampaing(Map<String, String> request) {
		Leads leads = new Leads();
		leads.setAddress1(request.get("address1"));
		leads.setAddress2(request.get("address2"));
		leads.setAddress3(request.get("address3"));
		leads.setAlternatePhonenumber(request.get("alternatePhonenumber"));
		leads.setCity(request.get("city"));
		leads.setCountryCode(request.get("countryCode"));
		leads.setEmail(request.get("email"));
		leads.setFirstName(request.get("firstName"));
		leads.setGender(request.get("gender"));
		leads.setLastName(request.get("lastName"));
		leads.setMiddleName(request.get("middleName"));
		leads.setPhoneNumber(request.get("phoneNumber"));
		leads.setState(request.get("state"));
		leads.setPostalCode(request.get("postalCode"));
		leads.setStatus(request.get("status"));
		leadRepository.save(leads);
	}
	
	public void attachLeadToCampaing(Map<String, String> request) {
		Optional<Leads> leads = leadRepository.findById(Integer.parseInt(request.get("lead_id")));
		if (leads.isPresent()) {
			Leads lead = leads.get();
			Optional<Campaing> campaing = campaingRepository.findById(Integer.parseInt(request.get("campaing_id")));
			if (campaing.isPresent()) {
				Campaing campaingFound= new Campaing();
				CampaingLeadMapping campaingLeadMapping= new CampaingLeadMapping();
				campaingLeadMapping.setCampaingid(campaingFound.getId());
				campaingLeadMapping.setLeadid(lead.getId());
				campaingLeadMappingRepository.save(campaingLeadMapping);
			}
		}
	}
}

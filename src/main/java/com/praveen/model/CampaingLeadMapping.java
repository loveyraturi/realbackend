package com.praveen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "CampaingLeadMapping")
public class CampaingLeadMapping {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	int campaingid;
	int leadid;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getCampaingid() {
		return campaingid;
	}
	public void setCampaingid(int campaingid) {
		this.campaingid = campaingid;
	}
	public int getLeadid() {
		return leadid;
	}
	public void setLeadid(int leadid) {
		this.leadid = leadid;
	}
	
}

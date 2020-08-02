package com.praveen.model;

import java.sql.Timestamp;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "call_logs")
public class CallLogs {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String assignedTo;
	int leadId;
	String callDuration;
	String status;
	String callBackDateTime;
	String comments;
	Date callDate;
	Date CallEndDate;
	
	
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCallBackDateTime() {
		return callBackDateTime;
	}
	public void setCallBackDateTime(String callBackDateTime) {
		this.callBackDateTime = callBackDateTime;
	}
	public String getComments() {
		return comments;
	}
	public void setComments(String comments) {
		this.comments = comments;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getAssignedTo() {
		return assignedTo;
	}
	public void setAssignedTo(String assignedTo) {
		this.assignedTo = assignedTo;
	}
	public int getLeadId() {
		return leadId;
	}
	public void setLeadId(int leadId) {
		this.leadId = leadId;
	}
	public String getCallDuration() {
		return callDuration;
	}
	public void setCallDuration(String callDuration) {
		this.callDuration = callDuration;
	}
	public Date getCallDate() {
		return callDate;
	}
	public void setCallDate(Date callDate) {
		this.callDate = callDate;
	}
	public Date getCallEndDate() {
		return CallEndDate;
	}
	public void setCallEndDate(Date callEndDate) {
		CallEndDate = callEndDate;
	}
	
}

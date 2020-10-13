package com.praveen.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "payments")
public class Payments {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	int id;
	String mihpayid;
	String status;
	String mode;
	String txnid;
	String bankcode;
	String PG_TYPE;
	String bank_ref_num;
	String amount;
	String addedon;
	String productinfo;
	String email;
	String payuMoneyId;
	String username;
	String paymentPlan;
	String paymentType;
	
	
	public String getPaymentPlan() {
		return paymentPlan;
	}
	public void setPaymentPlan(String paymentPlan) {
		this.paymentPlan = paymentPlan;
	}
	public String getPaymentType() {
		return paymentType;
	}
	public void setPaymentType(String paymentType) {
		this.paymentType = paymentType;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getMihpayid() {
		return mihpayid;
	}
	public void setMihpayid(String mihpayid) {
		this.mihpayid = mihpayid;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getMode() {
		return mode;
	}
	public void setMode(String mode) {
		this.mode = mode;
	}
	public String getTxnid() {
		return txnid;
	}
	public void setTxnid(String txnid) {
		this.txnid = txnid;
	}
	public String getBankcode() {
		return bankcode;
	}
	public void setBankcode(String bankcode) {
		this.bankcode = bankcode;
	}
	public String getPG_TYPE() {
		return PG_TYPE;
	}
	public void setPG_TYPE(String pG_TYPE) {
		PG_TYPE = pG_TYPE;
	}
	public String getBank_ref_num() {
		return bank_ref_num;
	}
	public void setBank_ref_num(String bank_ref_num) {
		this.bank_ref_num = bank_ref_num;
	}
	public String getAmount() {
		return amount;
	}
	public void setAmount(String amount) {
		this.amount = amount;
	}
	public String getAddedon() {
		return addedon;
	}
	public void setAddedon(String addedon) {
		this.addedon = addedon;
	}
	public String getProductinfo() {
		return productinfo;
	}
	public void setProductinfo(String productinfo) {
		this.productinfo = productinfo;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPayuMoneyId() {
		return payuMoneyId;
	}
	public void setPayuMoneyId(String payuMoneyId) {
		this.payuMoneyId = payuMoneyId;
	}
}

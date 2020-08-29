package com.praveen.model;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table(name = "images")
public class Images {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
    int id;
	String imageName;
	int propertyId;
	int isBanner;
	
	public int getIsBanner() {
		return isBanner;
	}
	public void setIsBanner(int isBanner) {
		this.isBanner = isBanner;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getImageName() {
		return imageName;
	}
	public void setImageName(String imageName) {
		this.imageName = imageName;
	}
	public int getPropertyId() {
		return propertyId;
	}
	public void setPropertyId(int propertyId) {
		this.propertyId = propertyId;
	}

}

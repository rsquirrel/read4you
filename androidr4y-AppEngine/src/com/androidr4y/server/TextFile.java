package com.androidr4y.server;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class TextFile {

	@Id
	private String blobKey;	//the corresponding blob key
	
	private String filename;
	private String category;
	private String owner;
	private String req_type;
	private Date time;
	
	public String getFilename() {
		return filename;
	}
	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String getCategory() {
		return category;
	}
	public void setCategory(String category) {
		this.category = category;
	}
	
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	
	public String getReq_type() {
		return req_type;
	}
	public void setReq_type(String req_type) {
		this.req_type = req_type;
	}
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}
	
	public String getBlobKey() {
		return blobKey;
	}
}

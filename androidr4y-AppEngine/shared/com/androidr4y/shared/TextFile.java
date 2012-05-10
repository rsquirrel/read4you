package com.androidr4y.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TextFile {
	static private DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	private String blobKey;	//the corresponding blob key
	private String strKey;	//the corresponding str key
	
	private String filename;
	private String category;
	private String owner;
	private String req_type;
	private Date time;
	private int num_audio;
	
	public TextFile(String k, String bk) {
		strKey = k;
		blobKey = bk;
	}
	
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
	
	public String getReqType() {
		return req_type;
	}
	public void setReqType(String req_type) {
		this.req_type = req_type;
	}
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	public int getNumAudio() {
		return num_audio;
	}
	public void setNumAudio(int num_audio) {
		this.num_audio = num_audio;
	}

	public String getStrKey() {
		return strKey;
	}
	public String getBlobKey() {
		return blobKey;
	}
	
	public String toString() {
		
		return (strKey + "\n" +
				filename + "\n" +
				blobKey + "\n" +
				category + "\n" +
				owner + "\n" +
				req_type + "\n" +
				dateFormat.format(time) +
				num_audio);
	}
	
	public void parse(String str) {
		String[] strlist = str.split("\n");
		strKey = strlist[0];
		filename = strlist[1];
		blobKey = strlist[2];
		category = strlist[3];
		owner = strlist[4];
		req_type = strlist[5];
		try {
			time = dateFormat.parse(strlist[6]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			time = new Date();
		}
		num_audio = Integer.parseInt(strlist[6]);
	}
}

package com.androidr4y.shared;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioFile {
	private static DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");

	private String blobKey;	//the corresponding blob key
	
	private String usage;
	private String uploader;
	private Date time;
	
	public AudioFile(String bk) {
		blobKey = bk;
	}
	
	public String getUsage() {
		return usage;
	}
	public void setUsage(String usage) {
		this.usage = usage;
	}
	
	public Date getTime() {
		return time;
	}
	public void setTime(Date time) {
		this.time = time;
	}

	public String getUploader() {
		return uploader;
	}
	public void setUploader(String uploader) {
		this.uploader = uploader;
	}
	
	public String getBlobKey() {
		return blobKey;
	}
	
	public String toString() {
		StringBuilder result = new StringBuilder();
		
		result.append(blobKey + "$\n$");
		result.append(usage + "$\n$");
		result.append(uploader + "$\n$");
		result.append(dateFormat.format(time));
		System.err.println(result.toString());
		
		return result.toString();
	}
	
	public void parse(String str) {
		String[] strlist = str.split("$\n$");
		blobKey = strlist[0];
		usage = strlist[1];
		uploader = strlist[2];
		try {
			time = dateFormat.parse(strlist[3]);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			time = new Date();
		}
	}
}

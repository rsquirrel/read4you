package com.androidr4y.server;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class AudioFile {

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
		DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
		return (blobKey + "\n" +
				usage + "\n" +
				uploader + "\n" +
				dateFormat.format(time));
	}
}

package com.androidr4y.server;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class UserRoot {

	@Id
	private String userID;
	
	private String email;
	private String last_audio;
	
	public String getUserID() {
		return userID;
	}
	
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getLast_audio() {
		return last_audio;
	}
	public void setLast_audio(String last_audio) {
		this.last_audio = last_audio;
	}
}

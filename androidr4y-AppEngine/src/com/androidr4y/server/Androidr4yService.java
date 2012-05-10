package com.androidr4y.server;

import java.util.ArrayList;
import java.util.List;

import api.CachedQuery;

import com.androidr4y.annotation.ServiceMethod;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class Androidr4yService {
	
	//private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	//private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	private UserService userService = UserServiceFactory.getUserService();
	
	
	@ServiceMethod
	public List<String> getFileList() {
		ArrayList<String> result = new ArrayList<String>();
		int limit = 20;
		int offset = 0;
		User user = userService.getCurrentUser();
		Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
		CachedQuery fileQuery = new CachedQuery(rootKey, "TextFile");
		List<Entity> results = fileQuery.getList(limit, offset);
		for (Entity fileInfo : results) {			//for each file, generate an entry
			//CachedQuery audioQuery = new CachedQuery(fileInfo.getKey(), "AudioFile");
			//int numAudio = audioQuery.getCount();
			result.add(fileInfo.getProperty("filename").toString());
		}
		return result;
	}

/*
	@ServiceMethod
	public UserRoot createUserRoot() {
		// TODO Auto-generated method stub
		return null;
	}
	@ServiceMethod
	public UserRoot readUserRoot(Long id) {
		// TODO Auto-generated method stub
		return null;
	}

	@ServiceMethod
	public UserRoot updateUserRoot(UserRoot userroot) {
		// TODO Auto-generated method stub
		return null;
	}

	@ServiceMethod
	public void deleteUserRoot(UserRoot userroot) {
		// TODO Auto-generated method stub

	}

	@ServiceMethod
	public List<UserRoot> queryUserRoots() {
		// TODO Auto-generated method stub
		return null;
	}
*/
}

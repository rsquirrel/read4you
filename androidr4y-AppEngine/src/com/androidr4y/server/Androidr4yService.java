package com.androidr4y.server;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import api.CachedQuery;
import api.Notification;

import com.androidr4y.annotation.ServiceMethod;
import com.androidr4y.shared.AudioFile;
import com.androidr4y.shared.TextFile;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;


public class Androidr4yService {
	
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	private UserService userService = UserServiceFactory.getUserService();

	@ServiceMethod
	public List<String> getFileList() {
		//ArrayList<TextFile> result = new ArrayList<TextFile>();
		ArrayList<String> result = new ArrayList<String>();
		int limit = 20;
		int offset = 0;
		User user = userService.getCurrentUser();
		Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
		CachedQuery fileQuery = new CachedQuery(rootKey, "TextFile");
		List<Entity> results = fileQuery.getList(limit, offset);
		for (Entity fileInfo : results) {			//for each file, generate an entry
			
			TextFile tf = new TextFile(
					KeyFactory.keyToString(fileInfo.getKey()),
					fileInfo.getKey().getName());

			if (fileInfo.getProperty("filename") == null) {
				tf.setFilename(" ");
			} else {
				tf.setFilename(fileInfo.getProperty("filename").toString());
			}
			if (fileInfo.getProperty("owner") == null) {
				tf.setOwner(" ");
			} else {
				tf.setOwner(fileInfo.getProperty("owner").toString());
			}
			if (fileInfo.getProperty("category") == null) {
				tf.setCategory(" ");
			} else {
				tf.setCategory(fileInfo.getProperty("category").toString());
			}
			if (fileInfo.getProperty("req_type") == null) {
				tf.setReqType(" ");
			} else {
				tf.setReqType(fileInfo.getProperty("req_type").toString());
			}
			if (fileInfo.getProperty("time") == null) {
				tf.setTime(new Date());
			} else {
				tf.setTime((Date)fileInfo.getProperty("time"));
			}
			
			CachedQuery audioQuery = new CachedQuery(fileInfo.getKey(), "AudioFile");
			tf.setNumAudio(audioQuery.getCount());
			
			result.add(tf.toString());
		}
		return result;
	}

	@ServiceMethod
	public List<String> getSearchList(String owner, String filename,
			String category, String req_type) {
		//ArrayList<TextFile> result = new ArrayList<TextFile>();
		ArrayList<String> result = new ArrayList<String>();
		
		int limit = 20;
		int offset = 0;

		HashMap<String, String> filter = new HashMap<String, String>();
		filter.put("owner", owner);
		filter.put("filename", filename);
		filter.put("category", category);
		filter.put("req_type", req_type);
		CachedQuery fileQuery = new CachedQuery(filter);
		List<Entity> results = fileQuery.getList(limit, offset);
		
		for (Entity fileInfo : results) {			//for each file, generate an entry
			
			TextFile tf = new TextFile(
					KeyFactory.keyToString(fileInfo.getKey()),
					fileInfo.getKey().getName());

			if (fileInfo.getProperty("filename") == null) {
				tf.setFilename(" ");
			} else {
				tf.setFilename(fileInfo.getProperty("filename").toString());
			}
			if (fileInfo.getProperty("owner") == null) {
				tf.setOwner(" ");
			} else {
				tf.setOwner(fileInfo.getProperty("owner").toString());
			}
			if (fileInfo.getProperty("category") == null) {
				tf.setCategory(" ");
			} else {
				tf.setCategory(fileInfo.getProperty("category").toString());
			}
			if (fileInfo.getProperty("req_type") == null) {
				tf.setReqType(" ");
			} else {
				tf.setReqType(fileInfo.getProperty("req_type").toString());
			}
			if (fileInfo.getProperty("time") == null) {
				tf.setTime(new Date());
			} else {
				tf.setTime((Date)fileInfo.getProperty("time"));
			}
			
			CachedQuery audioQuery = new CachedQuery(fileInfo.getKey(), "AudioFile");
			tf.setNumAudio(audioQuery.getCount());
			
			result.add(tf.toString());
		}
		return result;
	}
	
	@ServiceMethod
	public List<String> getAudioList(String fileKey) {
		//ArrayList<AudioFile> result = new ArrayList<AudioFile>();
		ArrayList<String> result = new ArrayList<String>();
		int limit = 20;
		int offset = 0;
		Key fk = KeyFactory.stringToKey(fileKey);
		CachedQuery audioQuery = new CachedQuery(fk, "AudioFile");
		List<Entity> results = audioQuery.getList(limit, offset);
		for (Entity fileInfo : results) {			//for each file, generate an entry
			//CachedQuery audioQuery = new CachedQuery(fileInfo.getKey(), "AudioFile");
			//int numAudio = audioQuery.getCount();
			AudioFile af = new AudioFile(fileInfo.getKey().getName());
			if (fileInfo.getProperty("uploader") == null) {
				af.setUploader(" ");
			} else {
				af.setUploader(fileInfo.getProperty("uploader").toString());
			}
			if (fileInfo.getProperty("usage") == null) {
				af.setUsage(" ");
			} else {
				af.setUsage(fileInfo.getProperty("usage").toString());
			}
			if (fileInfo.getProperty("time") == null) {
				af.setTime(new Date());
			} else {
				af.setTime((Date)fileInfo.getProperty("time"));
			}
			result.add(af.toString());
		}
		return result;
	}
	
	@ServiceMethod
	public String getUploadURL() {
		return blobstore.createUploadUrl("/upaudio");
	}
	
	@ServiceMethod
	public String sendEmail(String textID) {
		Notification notice = new Notification();
		Key textKey = KeyFactory.stringToKey(textID);
		String link = "http://androidr4y.appspot.com/read?bk=" + textID;
		try {
			String owner_email = (String)datastore.get(
					datastore.get(textKey).getParent()).getProperty("email");
			System.err.println("owner_email " + owner_email);
			notice.sendEmail(link, new Date(), owner_email);
			return "OK";
		} catch (EntityNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "ERR";
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

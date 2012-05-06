/*
 * Delete a text file, together with all the attached video files
 * @author: Yan Zou
 */


package api;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreFailureException;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class DeleteServlet extends HttpServlet {
    private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
			UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			
			String strKey = req.getParameter("bk");
	        BlobKey blobKey = new BlobKey(strKey);
    		try {
    			blobstore.delete(blobKey);
    		} catch (BlobstoreFailureException e) {
    			System.err.println("Failed to delete blob " + strKey);
    		}
	        
	        if (user != null) {
	        	Key fileKey = new KeyFactory.Builder("UserRoot", user.getUserId())
	        								.addChild("TextFile", strKey)
	        								.getKey();
	        	Query query = new Query("AudioFile");
	        	query.setAncestor(fileKey).setKeysOnly();
	        	PreparedQuery pq = datastore.prepare(query);
	        	ArrayList<Key> deleteKeys = new ArrayList<Key>();
	        	deleteKeys.add(fileKey);
	        	
	        	for (Entity audioEntity : pq.asIterable()) {
	        		Key key = audioEntity.getKey();
	        		deleteKeys.add(key);
	        		try {
	        			blobstore.delete(new BlobKey(key.getName()));
	        		} catch (BlobstoreFailureException e) {
	        			System.err.println("Failed to delete blob " + key.getName());
	        		}
	        	}
	        	
	        	datastore.delete(deleteKeys);
	        	
	        }
	        
	        resp.sendRedirect("/list");
	    }
}
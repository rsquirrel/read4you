package api;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
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
	        blobstore.delete(blobKey);
	        
	        if (user != null) {
	        	Key fileKey = new KeyFactory.Builder("UserRoot", user.getUserId())
	        								.addChild("TextFile", strKey)
	        								.getKey();
	        	datastore.delete(fileKey);
	        }
	        
	        resp.sendRedirect("/list");
	    }
}
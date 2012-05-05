
package api;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
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

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {
    private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        Map<String, List<BlobKey>> blobs = blobstore.getUploads(req);
        //System.out.println(blobs.toString());
        if (blobs != null && blobs.get("textFile") != null) {
	        BlobKey blobKey = blobs.get("textFile").get(0);
	        System.out.println(blobs.toString());
	        
	        UserService userService = UserServiceFactory.getUserService();
			User user = userService.getCurrentUser();
			if (user != null) {
				Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
				datastore.put(new Entity(rootKey));
				Entity fileInfo = new Entity("TextFile", blobKey.getKeyString(), rootKey);
				datastore.put(fileInfo);
			}
        }
		
		resp.sendRedirect("/list");
/*
        if (blobKey == null) {
            res.sendRedirect("/");
        } else {
            res.sendRedirect("/serve?blob-key=" + blobKey.getKeyString());
        }*/
    }
}

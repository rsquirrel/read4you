/*
 * The page after uploading the text file
 * Record all the file information into datastore
 * @author: Yan Zou
 */

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
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class UploadServlet extends HttpServlet {
    private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserService userService = UserServiceFactory.getUserService();

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {

        Map<String, List<BlobKey>> blobs = blobstore.getUploads(req);
        //System.out.println(blobs.toString());
        if (blobs != null && blobs.get("text_file") != null) {	//if text_file ever exists
        	//avoid exception when user directly click submit without specifying any files
	        BlobKey blobKey = blobs.get("text_file").get(0);	
	        //System.out.println("blobs: " + blobs.toString());
	        
			User user = userService.getCurrentUser();
			if (user != null) {		//record all the file info into user root entity
				Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
		    	try {
		    		datastore.get(rootKey);
		    	} catch (EntityNotFoundException e) {
		    		//create the root entity in case there isn't
		    		datastore.put(new Entity(rootKey));
		    	}
				String filename = req.getParameter("file_name");
				String reqType = req.getParameter("req_type");
				String category = req.getParameter("category");
				//if the filename is not specified, use the original filename
				if (filename == null || filename.compareTo("") == 0) {
					try {
						Key blobinfoKey = KeyFactory.createKey("__BlobInfo__",
								blobKey.getKeyString());	//get original filename
						Entity blobInfo = datastore.get(blobinfoKey);
						filename = blobInfo.getProperty("filename").toString();
						//System.out.println("filename: " + filename);
					} catch (EntityNotFoundException e) {
						// TODO Auto-generated catch block
						//e.printStackTrace();
					}
				}
				//create a new file info entity as a child of the user root
				Entity fileInfo = new Entity("TextFile", blobKey.getKeyString(), rootKey);
				fileInfo.setProperty("owner", user.getNickname());
				fileInfo.setProperty("filename", filename);
				fileInfo.setProperty("req_type", reqType);
				fileInfo.setProperty("category", category);
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

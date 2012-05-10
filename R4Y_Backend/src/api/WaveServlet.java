/*
 * Save the wav in blobstore
 * @author: Yan Zou
 */

package api;

import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.files.AppEngineFile;
import com.google.appengine.api.files.FileService;
import com.google.appengine.api.files.FileServiceFactory;
import com.google.appengine.api.files.FileWriteChannel;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class WaveServlet extends HttpServlet {
    //private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private UserService userService = UserServiceFactory.getUserService();

    public void doPost(HttpServletRequest req, HttpServletResponse resp)
        throws ServletException, IOException {
    	User user = userService.getCurrentUser();
    	if (user != null) {	//we don't allow a wave file to be uploaded without a user login
	    	resp.getWriter().println(req.getParameterMap().toString());
	    	//BufferedReader reader = req.getReader();	//read the wave file
	    	InputStream istream = req.getInputStream();
	    	byte[] bytes = new byte[req.getContentLength()];
	    	//istream.read(bytes);
	    	
	    	// Get a file service
	    	FileService fileService = FileServiceFactory.getFileService();
	    	// Create a new Blob file with mime-type "audio/wav"
	    	AppEngineFile file = fileService.createNewBlobFile("audio/wav");
	    	// Open a channel to write to it
	    	boolean lock = true;	// This time lock because we intend to finalize
	    	FileWriteChannel writeChannel = fileService.openWriteChannel(file, lock);
	    	//OutputStream ostream = Channels.newOutputStream(writeChannel);
	    	//PrintWriter out = new PrintWriter(Channels.newWriter(writeChannel, "UTF8"));
	    	
	    	//read the wave file
	    	//StringBuffer sb = new StringBuffer();
	    	//StringBuilder sb = new StringBuilder();
	    	int c;
	    	int i = 0;
	    	while ((c = istream.read()) != -1) {
	    		//sb.append((char)c);
	    		//ostream.write(c);
	    		bytes[i] = (byte) c;
	    		++i;
	    	}
	    	//write the file into blob file
	    	//System.out.println(sb.toString());
	    	writeChannel.write(ByteBuffer.wrap(bytes));
	    	//out.close();
	    	//ostream.write(sb.toString().getBytes("UTF8"));
	    	//ostream.close();
	    	writeChannel.closeFinally();
	    	
	    	BlobKey blobKey = fileService.getBlobKey(file);
	    	Key k = KeyFactory.createKey("UserRoot", user.getUserId());
	    	Entity userRoot;
	    	try {
	    		userRoot = Storage.get(k);

	    	} catch (EntityNotFoundException e) {
	    		userRoot = new Entity(k);
	    	}
	    	userRoot.setProperty("last_audio", blobKey.getKeyString());
	    	Storage.put(userRoot);
    	}
    }
}

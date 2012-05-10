package api;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;

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

/*
 * Deal with the wav post request.
 * Save the blob key and associate all the audio information with text in datastore
 */

@SuppressWarnings("serial")
public class UpWavServlet extends HttpServlet {
    private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	private UserService userService = UserServiceFactory.getUserService();
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		String textID = req.getParameter("text_file");
		Map<String, List<BlobKey>> blobs = blobstore.getUploads(req);
		
		//textID shouldn't be null, used to locate the text file and redirect
		if (textID != null && textID.compareTo("null") != 0 &&
			blobs != null && blobs.get("audio_file") != null) {
			
			Key textKey = KeyFactory.stringToKey(textID);
			BlobKey audioBlobKey = blobs.get("audio_file").get(0);
						
			String usage = req.getParameter("usage");
			User user = userService.getCurrentUser();
			String uploaderID;
			if (user == null) {
				uploaderID = null;
			} else {
				uploaderID = user.getNickname();
			}
			//req.getParameter("uploader");
			
			Entity audioEntity = new Entity("AudioFile",
					audioBlobKey.getKeyString(), textKey);
			//the third level UserRoot->TextFile->AudioFile
			audioEntity.setProperty("usage", usage);
			audioEntity.setProperty("uploader", uploaderID);
			audioEntity.setProperty("time", new Date());
			datastore.put(audioEntity);

			//send email for notification
			try {
				Notification notice = new Notification();
				String link = "http://" + req.getServerName() + ":"
						+ req.getServerPort() + "/read?bk=" + textID;
				String owner_email = (String)datastore.get(
						datastore.get(textKey).getParent()).getProperty("email");
				notice.sendEmail(link, new Date(), owner_email);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
			}
				
			//resp.sendRedirect("/read?bk=" + textID);
		} else {
			System.err.println("text file ID is null");
		}
	}
}

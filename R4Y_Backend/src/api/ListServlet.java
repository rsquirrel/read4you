/*
 * Main page - List of text files that user has uploaded for audio request
 * @author: Yan Zou
 */

package api;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
//import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

@SuppressWarnings("serial")
public class ListServlet extends HttpServlet {
    private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		
		//the following strings are used to construct an html webpage
		String head = "<html>\n<head>\n<title>Read4You</title>\n</head>\n<body>\n" +
				"<center>\n<div id=\"wrapper\"" +
				" style=\"width:600px;text-align:left;\">";
		String navBar = "";		//user control panel
		String uplForm = "";	//post new text files
		String uplResult = "";	//not used
		String fileList = "";	//the table of text files
		String bottom = "</div>\n</center>\n</body>\n</html>";
		
		if (user != null) {
			
			navBar = "<p>Current user: " + user.getNickname() + "&nbsp;&nbsp;<a href=\"" +
					userService.createLogoutURL("/") + "\">sign out</a></p>";
			uplForm = "<p><a href=\"/post\">Post New Text File</a></p>";
			/*
			uplForm = "<form action=\"" + blobstore.createUploadUrl("/upload") +
					"\" method=\"post\" enctype=\"multipart/form-data\">" +
					"<label for=\"textFile\">" +
					"Upload new text files here:" +
					"</label>" +
					"<input type=\"file\" name=\"textFile\" />" +
					"<input type=\"text\" name=\"tag\" />" +
					"<input type=\"submit\" value=\"Submit\" />" +
					"</form>";
			*/
			String uploadResult = req.getParameter("ur");
			if (uploadResult != null) {
				if (uploadResult.compareTo("1") == 0) {
					uplResult = "<p><font color=\"green\">"
							+ "File successfully uploaded!</font></p>";
				} else {
					uplResult ="<p><font color=\"red\">ERROR: " +
							"Failed to upload the file</font></p>";
				}
			}
			
			fileList = "<table width=600>\n<tr>\n<td width=200>File Name</td>\n" +
					"<td width=150>Category</td>\n<td width=150>Request Type</td>\n" +
					"<td width=50>Audio</td>\n<td width=50></td>\n</tr>";
			Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
			/*
			try {
				datastore.get(rootKey);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				datastore.put(new Entity(rootKey));
			}*/
			
			/******************************************
			 * Construct the table of text files
			 ******************************************/
			
			Query fileQuery = new Query("TextFile");	//query all the text files
			fileQuery.setAncestor(rootKey);				//under the user root
			FetchOptions fetchOp = FetchOptions.Builder.withDefaults();
			List<Entity> results = datastore.prepare(fileQuery).asList(fetchOp);
			for (Entity fileInfo : results) {			//for each file, generate an entry
				Query audioQuery = new Query("AudioFile");
				audioQuery.setAncestor(fileInfo.getKey());
				int numAudio = datastore.prepare(audioQuery).countEntities(fetchOp);
				fileList += "<tr>\n<td><a href=\"/serve?bk=" + fileInfo.getKey().getName() +
						"\">" + fileInfo.getProperty("filename") + "</a></td>\n<td>" +
						fileInfo.getProperty("category") + "</td>\n<td>" +
						fileInfo.getProperty("req_type") + "</td>\n<td>" +
						numAudio + "</td>\n<td>" +
						"<a href=\"delete?bk=" + fileInfo.getKey().getName() + "\">" +
						"delete</a></td>\n</tr>\n";
			}
			fileList += "</ul>";
		} else {
			navBar = "<p>Welcome to Read4You! <a href=\"" + userService.createLoginURL("/") +
					"\">Sign in or register</a> to start.</p>";
		}
		
		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println(head);
		out.println(navBar);
		out.println(uplForm);
		out.println(uplResult);
		out.println(fileList);
		out.println(bottom);
	}
}

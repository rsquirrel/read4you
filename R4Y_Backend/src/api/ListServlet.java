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
import com.google.appengine.api.datastore.EntityNotFoundException;
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
		
		String head = "<html>\n<head>\n<title>Read4You</title>\n</head>\n<body>";
		String navBar = "";
		String uplForm = "";
		String uplResult = "";
		String fileList = "";
		String bottom = "</body></html>";
		
		if (user != null) {
			
			navBar = "<p>Current user: " + user.getNickname() + "&nbsp;&nbsp;<a href=\"" +
					userService.createLogoutURL("/") + "\">sign out</a></p>";

			uplForm = "<form action=\"" + blobstore.createUploadUrl("/upload") +
					"\" method=\"post\" enctype=\"multipart/form-data\">" +
					"<label for=\"textFile\">" +
					"Upload new text files here:" +
					"</label>" +
					"<input type=\"file\" name=\"textFile\" />" +
					"<input type=\"submit\" value=\"Submit\" />" +
					"</form>";
			
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
			
			fileList = "<ul>\n";
			Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
			/*
			try {
				datastore.get(rootKey);
			} catch (EntityNotFoundException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				datastore.put(new Entity(rootKey));
			}*/
			
			Query fileQuery = new Query("TextFile");
			fileQuery.setAncestor(rootKey);
			List<Entity> results = datastore.prepare(fileQuery).asList(
					FetchOptions.Builder.withDefaults());
			for (Entity fileInfo : results) {
				fileList += "<li><a href=\"/serve?bk=" + fileInfo.getKey().getName() +
						"\">" + fileInfo.getKey().getName() + "</a>&nbsp;&nbsp;&nbsp;&nbsp;" +
						"<a href=\"delete?bk=" + fileInfo.getKey().getName() + "\">" +
						"delete</a></li>\n";
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
		out.println(fileList);
		out.println(bottom);
	}
}

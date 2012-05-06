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

/*
 * Display a text file.
 * Setup the WAMI recorder.
 * List the associated wav files.
 * 
 * WAMI recorder post to PostWav?text=....
 * 
 * @author: Shuai Sun
 */
public class ReadServlet extends HttpServlet
{
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		System.out.println("In Read get.");
		
		String text_id = req.getParameter("bk");

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();

		// the following strings are used to construct an html webpage
		String uplForm = ""; // post new text files
		String uplResult = ""; // not used
		String fileList = ""; // the table of text files
		String no_text_id = (text_id != null)
				? ("")
				: ("<p><font color=\"red\">No text Id is specified.</font></p>\n<br />\n"
						+ "<p>Please go back to <a href=\"/list\">List</a>.</p>");

		if (user != null && text_id != null)
		{
			//uplForm = "<p><a href=\"/post\">Post New Text File</a></p>";

			String uploadResult = req.getParameter("ur");
			if (uploadResult != null)
			{
				if (uploadResult.compareTo("1") == 0)
				{
					uplResult = "<p><font color=\"green\">"
							+ "File successfully uploaded!</font></p>";
				} else
				{
					uplResult = "<p><font color=\"red\">ERROR: "
							+ "Failed to upload the file</font></p>";
				}
			}

			Key textKey = KeyFactory.stringToKey(text_id);
			Entity textInfo = null;
			try
			{
				textInfo = datastore.get(textKey);
				//System.out.println(textKey.toString());
				//System.out.println(textInfo.toString());
			} catch (Exception e)
			{
			}

			/******************************************
			 * Construct the table of text area
			 ******************************************/

			String filename = "<h1>"
					+ textInfo.getProperty("filename").toString()
					+ "</h1>";
			
			String iframe = "<div><iframe "
					+ " src=\"/serve?bk=" + textKey.getName() + "\""
					+ " height=\"500px\" width=\"600px\""
					+ "></iframe></div>";
			
			fileList += filename;
			fileList += iframe;
			
			String wami_frame = "<div><iframe "
					+ " src=\"/wami.jsp?fk=" + textKey.toString() + "\""
					+ " height=\"200px\" width=\"600px\""
					+ "></iframe></div>"; 
			fileList += wami_frame;
			
		}

		resp.setContentType("text/html");
		PrintWriter out = resp.getWriter();
		out.println(ListServlet.HEAD);
		out.println(no_text_id);
		out.println(fileList);
		out.println(uplForm);
		out.println(uplResult);
		out.println(ListServlet.BOTTOM);

	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		System.out.println("In Read post.");
		System.out.println("req: " + req.toString());
	}
}
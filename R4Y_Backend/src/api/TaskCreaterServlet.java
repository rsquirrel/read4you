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
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.KeyFactory;

import com.google.appengine.api.taskqueue.Queue;
import com.google.appengine.api.taskqueue.QueueFactory;
import static com.google.appengine.api.taskqueue.TaskOptions.Builder.*;

public class TaskCreaterServlet extends HttpServlet {
    private BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	private DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
			
			try {
				Entity en = datastore.get(KeyFactory.stringToKey(req.getParameter("ak")));
				en.setProperty("processing", "1");
				datastore.put(en);
				System.out.println("Set to 1");
			} catch (EntityNotFoundException e) {
				resp.sendRedirect("/read?bk=" + req.getParameter("tbk"));
			}
			
	    	Queue queue = QueueFactory.getQueue("audioTaskQueue");
	    	queue.add(withUrl("/audioproc")
	    			.param("ak", req.getParameter("ak"))
	    			.param("tsk", req.getParameter("tsk")));
	        resp.sendRedirect("/read?bk=" + req.getParameter("tbk"));
	        
	    }
}
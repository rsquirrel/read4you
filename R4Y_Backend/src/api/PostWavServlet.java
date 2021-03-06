package api;

import java.io.IOException;
import java.util.Date;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.EntityNotFoundException;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.datastore.Transaction;
import com.google.appengine.api.datastore.TransactionOptions;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

/*
 * Deal with the wav post request.
 * Save the blob key and associate all the audio information with text in datastore
 */

@SuppressWarnings("serial")
public class PostWavServlet extends HttpServlet
{
	private UserService userService = UserServiceFactory.getUserService();
	private DatastoreService datastore = DatastoreServiceFactory
			.getDatastoreService();

	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		String textID = req.getParameter("text_file");

		// textID shouldn't be null, used to locate the text file and redirect
		if (textID != null && textID.compareTo("null") != 0)
		{
			try
			{
				Key textKey = KeyFactory.stringToKey(textID);

				Key owner_key = textKey.getParent();
				
				User user = userService.getCurrentUser();
				if (user != null)
				{ // only allow when user logs in
					Key rootKey = KeyFactory.createKey("UserRoot",
							user.getUserId());
					Entity rootEntity;
					try
					{
						rootEntity = Storage.get(rootKey);

					} catch (EntityNotFoundException e)
					{
						System.err.println("user root entity not found: ");
						rootEntity = new Entity(rootKey);
						rootEntity.setProperty("email", user.getEmail());
						Storage.put(rootEntity);
					}
					Object t = rootEntity.getProperty("last_audio");
					Object t2 = rootEntity.getProperty("last_audio_len");
					if (t != null)
					{ // there is an audio file recorded
						Transaction txn = datastore.beginTransaction(TransactionOptions.Builder.withXG(true)); // begin
																		// a
																		// transaction
						try
						{
							String audioBlobKey = t.toString();

							rootEntity.setProperty("last_audio", null);
							rootEntity.setProperty("last_audio_len", null);
							Storage.put(txn, rootEntity);

							String usage = req.getParameter("usage");
							String uploaderID = req.getParameter("uploader");

							// System.out.println(audioBlobKey);
							// System.out.println(textID);
							// System.out.println(resType);
							// System.out.println(uploaderID);

							Entity audioEntity = new Entity("AudioFile",
									audioBlobKey, textKey);
							// the third level UserRoot->TextFile->AudioFile
							audioEntity.setProperty("usage", usage);
							audioEntity.setProperty("uploader", uploaderID);
							audioEntity.setProperty("time", new Date());
							audioEntity.setProperty("length", t2);
							audioEntity.setUnindexedProperty("processing", "0");
							Storage.put(txn, audioEntity);

							Notification notice = new Notification();
							String link = "http://" + req.getServerName() + ":"
									+ req.getServerPort() + "/read?bk="
									+ textID;
							String owner_email = (String) Storage.get(
									Storage.get(textKey).getParent())
									.getProperty("email");
							notice.sendEmail(link, new Date(), owner_email);

							txn.commit();
						} finally
						{
							if (txn.isActive())
							{
								txn.rollback();
							}
						}

						// resp.sendRedirect("/serve?bk=" + audioBlobKey);
					} else
					{
						System.err.println("no audio file recorded");
					}
				}
				resp.sendRedirect("/read?bk=" + textID);

			} catch (IllegalArgumentException e)
			{
				e.printStackTrace(System.err);
			} catch (EntityNotFoundException e)
			{
				e.printStackTrace(System.err);
			}
		} else
		{
			System.err.println("text file ID is null");
		}
	}
}

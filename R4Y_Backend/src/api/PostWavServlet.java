package api;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/*
 * Deal with the wav post request.
 * Save the wav in BLOB store
 * redirect to AssociateInfo. include the blob key here
 */
public class PostWavServlet extends HttpServlet
{
	
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException
	{
		
	}
}

<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="java.util.List" %>

<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	
	String text_id = request.getParameter("bk");
	
	// the following strings are used to construct an html webpage
	String wavlist = "";	// list of the audio files
	String filename = "";
	
	Key textKey = null;
	if (user != null && text_id != null)
	{
		textKey = KeyFactory.stringToKey(text_id);
		Entity textInfo = null;
		try {
			textInfo = datastore.get(textKey);
		
			/******************************************
			 * Construct the list of wav files
			 ******************************************/
			 
			filename = "<h1>"
					+ textInfo.getProperty("filename").toString()
					+ "</h1>";
			
			Query audioQuery = new Query("AudioFile");
			audioQuery.setAncestor(textKey);
			FetchOptions fetchOp = FetchOptions.Builder.withDefaults();
			List<Entity> results = datastore.prepare(audioQuery).asList(fetchOp);
			wavlist = "";
	
			for (Entity e: results)
			{
				wavlist += "<p><a href=\"/serve?bk=" + e.getKey().getName() + "\" type=\"audio/ogg\">" +
						e.getProperty("usage") + " (by " + e.getProperty("uploader") + ")</a></p>\n";
				
			}
			wavlist += "<p><a href=\"http://mediaplayer.yahoo.com/example3.mp3\">yahoo</a></p>\n";
		} catch (Exception e) {
			filename = "File name not found";
			wavlist = "";
		}
	}
%>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>
	<title>Read File</title>

	<!-- swfobject is a commonly used library to embed Flash content -->
	<script type="text/javascript"
		src="https://ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js"></script>
	
	<!-- Setup the recorder interface -->
	<script type="text/javascript" src="/wami/recorder.js"></script>
	
	<!-- GUI code... take it or leave it -->
	<script type="text/javascript" src="/wami/gui.js"></script>
	
	<script>
		function setupRecorder() {
			Wami.setup({
				id : "wami",
				onReady : setupGUI
			});
		}
	
		function setupGUI() {
			var gui = new Wami.GUI({
				id : "wami",
				recordUrl : "/wav",
			});
	
			gui.setPlayEnabled(false);
		}
	</script>
</head>

<body onload="setupRecorder()">
   	<div id="wrapper" align="center">
    	<% if (user == null) { %>
    		<p>Welcome to Read4You!
    			<a href="<%= userService.createLoginURL("/list") %>">Sign in or register</a>
    		</p>
    	<% } else { %>
    		<p><span style="padding-left:15px;padding-right:15px">Hi, <%= user.getNickname() %></span>
    			|<span style="padding-left:15px;padding-right:15px">
    				<a href="/list">My Files</a>
    			</span>
    			|<span style="padding-left:15px;padding-right:15px">
    				<a href="/search">Search All Files</a>
    			</span>
    			|<span style="padding-left:15px;padding-right:15px">
    				<a href="<%= userService.createLogoutURL("/list") %>">Log Out</a>
    			</span>
    		</p>
    		<hr width=800 />
    		<% if (text_id == null) { %>
    		<p><font color="red">No text Id is specified.</font></p>
    		<br /><p>Please go back to <a href="/list">List</a>.</p>
    		<% } else { %>
    		<h1><%= filename %></h1>
			<table border=1>
				<tr>
					<td rowspan=2>
						<div><embed src="/serve?bk=<%= textKey.getName() %>" height="500px" width="600px" /></div>
					</td>
					<td>
						<form action="/postwav" method="post">
							<table>
								<tr>
									<td rowspan=2 style="padding-left:30px; padding-top:40px; padding-bottom:40px;">
										<div id="wami" style="position:relative;"></div>
										<noscript>WAMI requires Javascript</noscript>
									</td>
									<td style="padding-top:40px; padding-right:30px;">
										<select name="usage">
											<option>Read</option>
											<option>Comment</option>
											<option>Explain</option>
											<option>Translate</option>
										</select>
									</td>
								</tr>
								<tr>
									<td style="padding-bottom:40px; padding-right:30px;">
										<input type="hidden" name="text_file" value="<%=
											(textKey != null)?KeyFactory.keyToString(textKey):"null" %>" />
										<input type="hidden" name="uploader" value="<%= user.getNickname() %>" />
										<input id="submit" type="submit" name="submit" />
									</td>
								</tr>
							</table>
						</form>
					</td>
				</tr>
				<tr>
					<td style="vertical-align:top; width:220px"><%= wavlist %></td>
				</tr>
			</table>
			<br />
			<% }
    	} %>
</div>

<script type="text/javascript" src="http://webplayer.yahooapis.com/player.js"></script> 

</body>
</html>
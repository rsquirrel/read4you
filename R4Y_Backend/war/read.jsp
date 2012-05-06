<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>

<script type="text/javascript" src="http://mediaplayer.yahoo.com/js"></script>
<script type="text/javascript" src="http://webplayer.yahooapis.com/player.js"></script> 

<!-- swfobject is a commonly used library to embed Flash content -->
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js"></script>

<!-- Setup the recorder interface -->
<script type="text/javascript" src="/wami/recorder.js"></script>

<!-- GUI code... take it or leave it -->
<script type="text/javascript" src="/wami/gui.js"></script>

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
	String head =
		((UserServiceFactory.getUserService().getCurrentUser() == null)
		? ("Welcome to Read4You! <a href=\"" 
				+ UserServiceFactory.getUserService().createLoginURL("/list") + "\">Sign in or register</a> to start.")
		: ("Hi, " + UserServiceFactory.getUserService().getCurrentUser().getNickname() 
				+ "<span style=\"padding-left:15px\"><a href=\"/list\">Main </a></span>"
				+ "<span style=\"padding-left:15px\"><a href=\"" 
					+ UserServiceFactory.getUserService().createLogoutURL("/list") + "\">Log Out</a></span>"));
	String iframe = ""; // the table of text files
	String wavlist= "";	// list of the audio files
	String no_text_id = "<p><font color=\"red\">No text Id is specified.</font></p>\n<br />\n"
					+ "<p>Please go back to <a href=\"/list\">List</a>.</p>";
	
	Key textKey = null;
	if (user != null && text_id != null)
	{
		textKey = KeyFactory.stringToKey(text_id);
		Entity textInfo = null;
		try
		{
			textInfo = datastore.get(textKey);
		} catch (Exception e)
		{
		}
	
		/******************************************
		 * Construct the table of text area
		 ******************************************/
		String filename = "<h1>"
				+ textInfo.getProperty("filename").toString()
				+ "</h1>";
		
		iframe = "<div><iframe "
				+ " src=\"/serve?bk=" + textKey.getName() + "\""
				+ " height=\"500px\" width=\"600px\""
				+ "></iframe></div>";
		
		Query audioQuery = new Query("AudioFile");
		audioQuery.setAncestor(textKey);
		FetchOptions fetchOp = FetchOptions.Builder.withDefaults();
		List<Entity> results = datastore.prepare(audioQuery).asList(fetchOp);
		wavlist = "";
				//+ "<ul>\n";
		for (Entity e: results)
		{
			String usage = e.getProperty("res_type").toString();
			// <p><a href="song.mp3">Play Song</a></p>
			wavlist += "<p><a href=\"/serve?bk=" + e.getKey().getName() + "\">" + usage + "</a></p>\n";
			
		}
		// wavlist += "</ul>\n";
		
	}
%>

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
			//playUrl : 
			//singleButton : "yes"
		});

		gui.setPlayEnabled(false);
	}
</script>
</head>

<body onload="setupRecorder()">
<div id="wrapper" align=center>
<p > <%= head %> </p> 
<% if (text_id == null) { %>
	<%= no_text_id %>
	<%} else {%>
	<table border=1>
		<tr>
			<td><%= iframe %></td>
			<td><%= wavlist %></td>
		</tr>
	</table>
	
	
	
	<form action="/postwav" method="post">
		<div id="wami"></div>
		<noscript>WAMI requires Javascript</noscript>
		<input type="text" name="res_type" />
		<input type="hidden" name="text_file" value="<%= (textKey != null)?KeyFactory.keyToString(textKey):"null" %>" />
		<input type="hidden" name="uploader" value="<%= user.getUserId() %>" />
		<br /><input id="submit" type="submit" name="submit" />
	</form>
	<%} %>
</div>
</body>
</html>
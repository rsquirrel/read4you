<%@ page import="api.CachedQuery" %>
<%@ page import="api.UtilsClass" %>

<%@ page import="java.util.List" %>

<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreService" %>
<%@ page import="com.google.appengine.api.datastore.DatastoreServiceFactory" %>
<%@ page import="com.google.appengine.api.datastore.Entity" %>
<%@ page import="com.google.appengine.api.datastore.FetchOptions" %>
<%@ page import="com.google.appengine.api.datastore.Key" %>
<%@ page import="com.google.appengine.api.datastore.KeyFactory" %>
<%@ page import="com.google.appengine.api.datastore.Query" %>
<%@ page import="com.google.appengine.api.users.User" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>

<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
	BlobstoreService blobstore = BlobstoreServiceFactory.getBlobstoreService();
	UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();

	String navBar = "";
	String fileList = "";
	
	if (user != null) {
		
		int limit = 5;
		int page_num = UtilsClass.convertPageNum(request.getParameter("page"));
		int offset = (page_num - 1) * limit;
		
		/******************************************
		 * Construct the navigation bar (pages)
		 ******************************************/
		
		//query all the text files
		Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
		CachedQuery fileQuery = new CachedQuery(rootKey, "TextFile");	//under the user root
		
		int numPages = (fileQuery.getCount() - 1) / limit + 1;
		navBar = "<table>\n<col width=100><col width=400><col width=100>\n<tr>\n<td align=\"left\">";
		if (page_num > 1) {
			navBar += "<a href=\"/list?page=" + (page_num - 1) + "\">Prev Page</a>";
		}
		navBar += "</td>\n<td align=\"center\">Page&nbsp;" + 
			"<input type=\"text\" name=\"page\" maxlength=3 style=\"width:30px;text-align:right;\" value=\"" +
			page_num + "\">/" + numPages + "</td>\n<td align=\"right\">";
		if (page_num < numPages) {
			navBar += "<a href=\"/list?page=" + (page_num + 1)+ "\">Next Page</a>";
		}
		navBar += "</td></table>";
	
		/******************************************
		 * Construct the table of text files
		 ******************************************/
		 
		List<Entity> results = fileQuery.getList(limit, offset);
		for (Entity fileInfo : results) {			//for each file, generate an entry
			CachedQuery audioQuery = new CachedQuery(fileInfo.getKey(), "AudioFile");
			int numAudio = audioQuery.getCount();
			fileList += "<tr>\n<td><a href=\"/read?bk=" + KeyFactory.keyToString(fileInfo.getKey()) +
					"\">" + fileInfo.getProperty("filename") + "</a></td>\n<td>" +
					fileInfo.getProperty("category") + "</td>\n<td>" +
					fileInfo.getProperty("req_type") + "</td>\n<td>" +
					numAudio + "</td>\n<td>" +
					"<a href=\"delete?bk=" + fileInfo.getKey().getName() + "\">" +
					"delete</a></td>\n</tr>\n";
		}
	}
%>


<html>
    <head>
        <title>My Files</title>
    </head>
    <body>
    	<div id=wrapper align=center>
    	<% if (user == null) { %>
    		<p>Welcome to Read4You!
    			<a href="<%= userService.createLoginURL("/list") %>">Sign in or register</a>
    		</p>
    	<% } else { %>
    		<p><span style="padding-left:15px;padding-right:15px">Hi, <%= user.getNickname() %></span>
    			|<span style="padding-left:15px;padding-right:15px">My Files</span>
    			|<span style="padding-left:15px;padding-right:15px">
    				<a href="/search">Search All Files</a>
    			</span>
    			|<span style="padding-left:15px;padding-right:15px">
    				<a href="<%= userService.createLogoutURL("/list") %>">Log Out</a>
    			</span>
    		</p>
    		<hr width=600 />
    		<p style="width:600px;text-align:left;">
    			<a href="/posttext">Post New Text File</a>
			</p>
			<form action="/list" method="get"><%= navBar %></form>
			<table>
				<col width=200><col width=150><col width=150><col width=50><col width=50>
				<tr style="text-align:left">
					<th>File Name</th>
					<th>Category</th>
					<th>Request For</th>
					<th>Audio</th>
					<th></th>
				</tr>
				<%= fileList %>
			</table>
		<% } %>
        </div>
    </body>
</html>
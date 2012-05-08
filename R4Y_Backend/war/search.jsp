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

	String fileList = "";
	
	//get request parameters
	String owner = request.getParameter("owner");
	String filename = request.getParameter("filename");
	String category = request.getParameter("category");
	String req_type = request.getParameter("req_type");
	
	owner = ((owner == null) ? "" : owner);
	filename = ((filename == null) ? "" : filename);
	category = ((category == null) ? "" : category);
	req_type = ((req_type == null) ? "" : req_type);

	String select_blank = ((req_type.compareTo("") == 0) ? "selected" : "");
	String select_read = ((req_type.compareTo("Read") == 0) ? "selected" : "");
	String select_comment = ((req_type.compareTo("Comment") == 0) ? "selected" : "");
	String select_explain = ((req_type.compareTo("Explain") == 0) ? "selected" : "");
	String select_translate = ((req_type.compareTo("Translate") == 0) ? "selected" : "");
	
	if (user != null) {
		Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
		
		/******************************************
		 * Query the files according to the search conditions
		 ******************************************/
		
		Query fileQuery = new Query("TextFile");	//query all the text files
		//fileQuery.setAncestor(rootKey);				//under the user root
		if (owner.compareTo("") != 0) {	//if empty, not adding this filter
			fileQuery.addFilter("owner", Query.FilterOperator.EQUAL, owner);
		}
		if (filename.compareTo("") != 0) {	//if empty, not adding this filter
			fileQuery.addFilter("filename", Query.FilterOperator.EQUAL, filename);
		}
		if (category.compareTo("") != 0) {	//if empty, not adding this filter
			fileQuery.addFilter("category", Query.FilterOperator.GREATER_THAN_OR_EQUAL, category);
			fileQuery.addFilter("category", Query.FilterOperator.LESS_THAN, category + (char)255);
		}
		if (req_type.compareTo("") != 0) {	//if empty, not adding this filter
			fileQuery.addFilter("req_type", Query.FilterOperator.EQUAL, req_type);
		}
		
		FetchOptions fetchOp = FetchOptions.Builder.withDefaults();
		List<Entity> results = datastore.prepare(fileQuery).asList(fetchOp);
		
		/******************************************
		 * Construct the table of text files
		 ******************************************/
		 
		for (Entity fileInfo : results) {			//for each file, generate an entry
			Query audioQuery = new Query("AudioFile");
			audioQuery.setAncestor(fileInfo.getKey());
			int numAudio = datastore.prepare(audioQuery).countEntities(fetchOp);
			fileList += "<tr>\n<td>" + fileInfo.getProperty("owner") + "</td>\n<td>" +
					"<a href=\"/read?bk=" + KeyFactory.keyToString(fileInfo.getKey()) + "\">" +
					fileInfo.getProperty("filename") + "</a></td>\n<td>" +
					fileInfo.getProperty("category") + "</td>\n<td>" +
					fileInfo.getProperty("req_type") + "</td>\n<td>" +
					numAudio + "</td>\n</tr>\n";
		}
	}
%>


<html>
    <head>
        <title>Search Files</title>
    </head>
    <body>
    	<div id=wrapper align=center>
    	<% if (user == null) { %>
    		<p>Welcome to Read4You!
    			<a href="<%= userService.createLoginURL("/list") %>">Sign in or register</a>
    		</p>
    	<% } else { %>
    		<p><span style="padding-left:15px;padding-right:15px">Hi, <%= user.getNickname() %></span>
    			|<span style="padding-left:15px;padding-right:15px">
    				<a href="/list">My Files</a>
    			</span>
    			|<span style="padding-left:15px;padding-right:15px">Search All Files</span>
    			|<span style="padding-left:15px;padding-right:15px">
    				<a href="<%= userService.createLogoutURL("/list") %>"> | Log Out</a>
    			</span>
    		</p>
    		<hr width=600 />
    		<br />
    		<form action="/search" method="get">
				<table>
					<col width=150><col width=150><col width=125><col width=125><col width=50>
					<tr style="text-align:left">
						<th>Owner</th>
						<th>File Name</th>
						<th>Category</th>
						<th>Request For</th>
						<th>Audio</th>
					</tr>
					<tr style="text-align:left">
						<td><input type="text" size="20" name="owner" value=<%= owner %>></td>
						<td><input type="text" size="20" name="filename" value=<%= filename %>></td>
						<td><input type="text" size="15" name="category" value=<%= category %>></td>
						<td>
							<select name="req_type">
								<option <%= select_blank %>></option>
								<option <%= select_read %>>Read</option>
								<option <%= select_comment %>>Comment</option>
								<option <%= select_explain %>>Explain</option>
								<option <%= select_translate %>>Translate</option>
							</select>
						</td>
						<td><input type="submit" value="Search" /></td>
					</tr>
					<%= fileList %>
				</table>
			</form>
		<% } %>
        </div>
    </body>
</html>
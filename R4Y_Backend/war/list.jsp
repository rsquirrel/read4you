<%@ page import="api.CachedQuery" %>
<%@ page import="api.UtilsClass" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.Date" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
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
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			fileList += "<tr>\n<td><a href=\"/read?bk=" + KeyFactory.keyToString(fileInfo.getKey()) +
					"\">" + fileInfo.getProperty("filename") + "</a></td>\n<td>" +
					fileInfo.getProperty("category") + "</td>\n<td>" +
					fileInfo.getProperty("req_type") + "</td>\n<td>" +
					numAudio + "</td>\n<td>" +
					dateFormat.format((Date)(fileInfo.getProperty("time"))) + "</td>\n<td>" +
					"<a href=\"delete?bk=" + fileInfo.getKey().getName() + "\">" +
					"delete</a></td>\n</tr>\n";
		}
	}
%>

<!DOCTYPE html>
<html lang="en">
    <head>
    <meta charset="utf-8">
    <title>Bootstrap, from Twitter</title>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="">
    <meta name="author" content="">

    <!-- Le styles -->
    <link href="../stylesheets/css/bootstrap.css" rel="stylesheet">
    <style type="text/css">
      body {
        padding-top: 60px;
        padding-bottom: 40px;
      }
      .sidebar-nav {
        padding: 9px 0;
      }
    </style>
    <link href="../stylesheets/css/bootstrap-responsive.css" rel="stylesheet">

    <!-- Le HTML5 shim, for IE6-8 support of HTML5 elements -->
    <!--[if lt IE 9]>
      <script src="http://html5shim.googlecode.com/svn/trunk/html5.js"></script>
    <![endif]-->

    <!-- Le fav and touch icons -->
    <link rel="shortcut icon" href="../stylesheets/ico/favicon.ico">
    <link rel="apple-touch-icon-precomposed" sizes="144x144" href="../stylesheets/ico/apple-touch-icon-144-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="114x114" href="../stylesheets/ico/apple-touch-icon-114-precomposed.png">
    <link rel="apple-touch-icon-precomposed" sizes="72x72" href="../stylesheets/ico/apple-touch-icon-72-precomposed.png">
    <link rel="apple-touch-icon-precomposed" href="../stylesheets/ico/apple-touch-icon-57-precomposed.png">
  </head>
    
       <body>
    	<div id=wrapper align=center class="navbar navbar-fixed-top">
    	<div class="navbar-inner">
        <div class="container-fluid">
          <a class="btn btn-navbar" data-toggle="collapse" data-target=".nav-collapse">
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
            <span class="icon-bar"></span>
          </a>
          <a class="brand" href="#">Read4You</a>
          <div class="btn-group pull-right">
            <a class="btn dropdown-toggle" data-toggle="dropdown" href="#">
              <i class="icon-user"></i> Username
              <span class="caret"></span>
            </a>
            <ul class="dropdown-menu">
            <% if (user == null) { %>
            <li>        
<a href="<%= userService.createLoginURL("/list") %>">Sign in or register</a>
</li>
<% } else { %>
<li><a href="#"><%= user.getNickname() %></a></li>
<li class="divider"></li>
<li><a href="<%= userService.createLogoutURL("/list") %>">Sign Out</a></li>
<%} %>
            </ul>
            </div>
            <div class="nav-collapse">
            <ul class="nav">
              <li class="active"><a href="/list">My Files</a></li>
              <li><a href="/search">Search</a></li>
              <li><a href="/posttext">Upload</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>
    
    <div class="container">
    <% if (user == null) { %>
    <div class="hero-unit">
            <h1>Welcome to Read4You</h1>
            <p>Read4You allow you to upload any readable file for people around the world to read, share, or translate to other language for you.</p>
            <p><a class="btn btn-primary btn-large" href="<%= userService.createLoginURL("/list") %>">Sign in now &raquo;</a></p>
          </div>
           
<% } else { %>
<div class="span8 offset2">
<form class="well span4 offset1" action="/list" method="get"><%= navBar %></form>
			<table  class="table table-striped">
				<col width=200><col width=150><col width=150><col width=50><col width=50>
				<tr style="text-align:center">
					<th>File Name</th>
					<th>Category</th>
					<th>Request For</th>
					<th>Audio</th>
					<th>Uploaded On</th>
					<th></th>
				</tr>
				<%= fileList %>
			</table>
			
			</div>
<%} %>
           
    		
			
			</div>
	
    </body>
     <script src="../stylesheets/js/jquery.js"></script>
    <script src="../stylesheets/js/bootstrap-transition.js"></script>
    <script src="../stylesheets/js/bootstrap-alert.js"></script>
    <script src="../stylesheets/js/bootstrap-modal.js"></script>
    <script src="../stylesheets/js/bootstrap-dropdown.js"></script>
    <script src="../stylesheets/js/bootstrap-scrollspy.js"></script>
    <script src="../stylesheets/js/bootstrap-tab.js"></script>
    <script src="../stylesheets/js/bootstrap-tooltip.js"></script>
    <script src="../stylesheets/js/bootstrap-popover.js"></script>
    <script src="../stylesheets/js/bootstrap-button.js"></script>
    <script src="../stylesheets/js/bootstrap-collapse.js"></script>
    <script src="../stylesheets/js/bootstrap-carousel.js"></script>
    <script src="../stylesheets/js/bootstrap-typeahead.js"></script>
    
</html>
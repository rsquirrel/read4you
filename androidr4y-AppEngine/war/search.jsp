<%@ page import="api.CachedQuery" %>
<%@ page import="api.UtilsClass" %>

<%@ page import="java.util.List" %>
<%@ page import="java.util.HashMap" %>

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
	int page_num = 0;
	
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

		int limit = 5;
		page_num = UtilsClass.convertPageNum(request.getParameter("page"));
		int offset = (page_num - 1) * limit;
		
		/******************************************
		 * Query the files according to the search conditions
		 ******************************************/

		Key rootKey = KeyFactory.createKey("UserRoot", user.getUserId());
		HashMap<String, String> filter = new HashMap<String, String>();
		filter.put("owner", owner);
		filter.put("filename", filename);
		filter.put("category", category);
		filter.put("req_type", req_type);
		CachedQuery fileQuery = new CachedQuery(filter);

		/******************************************
		 * Construct the navigation bar (pages)
		 ******************************************/
		
		int numPages = (fileQuery.getCount() - 1) / limit + 1;
		String url = "/search?owner=" + owner + "&filname=" + filename + "&category=" +
			category + "&req_type=" + req_type + "&page=";
		navBar = "<table>\n<col width=100><col width=400><col width=100>\n<tr>\n<td align=\"left\">";
		if (page_num > 1) {
			navBar += "<a href=\"javascript: decreasePage();\">Prev Page</a>";
		}
		navBar += "</td>\n<td align=\"center\">Page&nbsp;" + 
			"<input type=\"text\" name=\"page\" maxlength=3 style=\"width:30px;text-align:right;\" value=\"" +
			page_num + "\">/" + numPages + "</td>\n<td align=\"right\">";
		if (page_num < numPages) {
			navBar += "<a href=\"javascript: increasePage();\">Next Page</a>";
		}
		navBar += "</td>\n</tr>\n</table>";
		
		/******************************************
		 * Construct the table of text files
		 ******************************************/
		 
		List<Entity> results = fileQuery.getList(limit, offset);
		 
		for (Entity fileInfo : results) {			//for each file, generate an entry
			CachedQuery audioQuery = new CachedQuery(fileInfo.getKey(), "AudioFile");
			int numAudio = audioQuery.getCount();
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
    
    <script type="text/javascript">
		function decreasePage() {
			pageForm = document.forms['page_form'];
			pageForm.elements['page'].value = <%= page_num - 1 %>
			pageForm.submit();
		}
		function increasePage() {
			pageForm = document.forms['page_form'];
			pageForm.elements['page'].value = <%= page_num + 1 %>
			pageForm.submit();
		}
	</script>
    
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
              <li ><a href="/list">My Files</a></li>
              <li class="active"><a href="/search">Search</a></li>
              <li><a href="/posttext">Upload</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>
    <div class="container offset1">
    
    		<form class="well span4 offset2" id="page_form" action="/search" method="get">
    			<%= navBar %>
    			<input type="hidden" name="owner" value="<%= owner %>" />
    			<input type="hidden" name="filename" value="<%= filename %>" />
    			<input type="hidden" name="category" value="<%= category %>" />
    			<input type="hidden" name="req_type" value="<%= req_type %>" />
    		</form>
    		<form class="well span8" action="/search" method="get">
				<table>
					<col width=150><col width=150><col width=125><col width=125><col width=50>
					<tr style="text-align:left">
						<th>Owner</th>
						<th>File Name</th>
						<th>Category</th>
						<th>Request</th>
						<th>Audio</th>
					</tr>
					<tr style="text-align:left">
						<td><input type="text" class="input-small" name="owner" value=<%= owner %>></td>
						<td><input type="text" class="input-small" name="filename" value=<%= filename %>></td>
						<td><input type="text" class="input-small" name="category" value=<%= category %>></td>
						<td>
							<select name="req_type" class="span1">
								<option <%= select_blank %>></option>
								<option <%= select_read %>>Read</option>
								<option <%= select_comment %>>Comment</option>
								<option <%= select_explain %>>Explain</option>
								<option <%= select_translate %>>Translate</option>
							</select>
						</td>
						<td><input type="checkbox" class="span2"></td>
						<td><button type="submit" class="btn btn-primary">Search</button></td>
					</tr>
					<%= fileList %>
				</table>
			</form>
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
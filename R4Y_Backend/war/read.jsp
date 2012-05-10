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
<%@ page import="api.CachedQuery" %>
<%@ page import="api.UtilsClass" %>
<%@ page import="java.text.DateFormat" %>
<%@ page import="java.text.SimpleDateFormat" %>
<%@ page import="api.Storage" %>

<%
	DatastoreService datastore = DatastoreServiceFactory.getDatastoreService();
    UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
	
	String text_id = request.getParameter("bk");
	
	// the following strings are used to construct an html webpage
	String wavlist = "";	// list of the audio files
	String filename = "";
	String navBar = "";
	
	Key textKey = null;
	if (user != null && text_id != null)
	{
		try {
			textKey = KeyFactory.stringToKey(text_id);
			Entity textInfo = null;
			textInfo = Storage.get(textKey);
		
			/******************************************
			 * Construct the list of wav files
			 ******************************************/
			 
			filename = "<h1>"
					+ textInfo.getProperty("filename").toString()
					+ "</h1>";

			CachedQuery fileQuery = new CachedQuery(textKey, "AudioFile");
			
			/******************************************
			 * Construct the navigation bar (pages)
			 ******************************************/
			 
			int limit = 5;
			int page_num = UtilsClass.convertPageNum(request.getParameter("page"));
			int offset = (page_num - 1) * limit;
			
			int numPages = (fileQuery.getCount() - 1) / limit + 1;
			String url = "/read?bk=" + text_id + "&page=";
			navBar = "<table>\n<col width=20><col width=170><col width=20>\n<tr>\n<td align=\"left\">";
			if (page_num > 1) {
				navBar += "<a href=\"" + url + (page_num - 1) + "\">&lt;</a>";
			}
			navBar += "</td>\n<td align=\"center\">Page&nbsp;" + 
				"<input type=\"text\" name=\"page\" maxlength=3 style=\"width:30px;text-align:right;\" value=\"" +
				page_num + "\">/" + numPages + "</td>\n<td align=\"right\">";
			if (page_num < numPages) {
				navBar += "<a href=\"" + url + (page_num + 1)+ "\">&gt;</a>";
			}
			navBar += "</td></table>";
			
			/******************************************
			 * Construct the audio file list
			 ******************************************/
			 
			
			
			
			List<Entity> results = fileQuery.getList(limit, offset);
			wavlist = "";
			DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
			for (Entity e: results)
			{
				System.out.println("in read" + e.getProperty("processing"));
				if (e.getProperty("processing").toString().equalsIgnoreCase("0")) {
					wavlist += "<p><a href=\"/serve?bk=" + e.getKey().getName() + "\" type=\"audio/ogg\">" +
							e.getProperty("usage") + " (by " + e.getProperty("uploader") + ")</a></p>\n";
							
					wavlist += "<p> :   : <a href=\"/createtask?ak=" + KeyFactory.keyToString(e.getKey()) + "&tbk=" + text_id + "&tsk=volup" + "\">" +
							"Vol Up</a> :   : <a href=\"/createtask?ak=" + KeyFactory.keyToString(e.getKey()) + "&tbk=" + text_id + "&tsk=voldn" + "\">" +
							"Vol Down</a> :   : </p>\n";
							
				}
				else {
					wavlist += "<p>" + e.getProperty("usage") + " (by " + e.getProperty("uploader") + ") Processing...</p>\n";
				}
				
			}
			wavlist += "<p><a href=\"http://mediaplayer.yahoo.com/example3.mp3\" type=\"audio/ogg\">yahoo</a></p>\n";
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
	<meta charset="utf-8">
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

	<!-- swfobject is a commonly used library to embed Flash content -->
	<script type="text/javascript"
		src="https://ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js"></script>
	
	<!-- Setup the recorder interface -->
	<script type="text/javascript" src="/wami/recorder.js"></script>
	
	<!-- GUI code... take it or leave it -->
	<script type="text/javascript" src="/wami/gui.js"></script>
	
	<script type="text/javascript">
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
              <li><a href="/search">Search</a></li>
              <li><a href="/posttext">Upload</a></li>
            </ul>
          </div><!--/.nav-collapse -->
        </div>
      </div>
    </div>

<div class="container offset2 span9">
    		<% if (text_id == null) { %>
    		<p><font color="red">No text Id is specified.</font></p>
    		<br /><p>Please go back to <a href="/list">List</a>.</p>
    		<% } else { %>
    		<h1 align="center"><%= filename %></h1>
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
										<select name="usage" class="span2">
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
										<button id="submit" type="submit" class="btn btn-primary">Submit</button>
									</td>
								</tr>
							</table>
						</form>
					</td>
				</tr>
				<tr>
					<td style="vertical-align:top; width:220px">
			    		<form action="/read" method="get">
			    			<%= navBar %>
			    			<input type="hidden" name="bk" value="<%= text_id %>" />
			    		</form>
						<%= wavlist %>
					</td>
				</tr>
			</table>
			<br />
			<% } %>
    

</div>
<script type="text/javascript" src="http://webplayer.yahooapis.com/player.js"></script> 

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
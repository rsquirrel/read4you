<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1//EN" "http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd">
<html>
<head>
<!-- swfobject is a commonly used library to embed Flash content -->
<script type="text/javascript"
	src="https://ajax.googleapis.com/ajax/libs/swfobject/2.2/swfobject.js"></script>

<!-- Setup the recorder interface -->
<script type="text/javascript" src="/wami/recorder.js"></script>

<!-- GUI code... take it or leave it -->
<script type="text/javascript" src="/wami/gui.js"></script>

<%@ page import="com.google.appengine.api.users.UserServiceFactory" %>
<%@ page import="com.google.appengine.api.users.UserService" %>
<%@ page import="com.google.appengine.api.users.User" %>

<%
    UserService userService = UserServiceFactory.getUserService();
	User user = userService.getCurrentUser();
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
	<div id="wami" style="margin-left: 100px;"></div>
	<noscript>WAMI requires Javascript</noscript>
	<form action="/postwav" method="post">
		<input type="text" name="res_type" />
		<input type="hidden" name="text_file" value="<%= request.getParameter("fk") %>" />
		<input type="hidden" name="uploader" value="<%= user.getUserId() %>" />
		<br /><input type="submit" name="submit" />
	</form>
</body>
</html>
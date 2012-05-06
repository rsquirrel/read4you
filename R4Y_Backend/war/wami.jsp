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

<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
	String url = blobstoreService.createUploadUrl("/read"); 
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
			recordUrl : <%= "\"/wave\""%>,
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
</body>
</html>
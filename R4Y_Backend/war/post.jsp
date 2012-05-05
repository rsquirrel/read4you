<!--
Text file upload page
@author: Yan Zou
-->

<%@ page import="com.google.appengine.api.blobstore.BlobstoreServiceFactory" %>
<%@ page import="com.google.appengine.api.blobstore.BlobstoreService" %>

<%
    BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
%>

<html>
    <head>
        <title>Upload Test</title>
    </head>
    <body>
        <form action="<%= blobstoreService.createUploadUrl("/upload") %>" method="post" enctype="multipart/form-data">
        	<table>
        		<tr>
        			<td><label for="text_file">File Name:</label></td>
        			<td><input type="text" name="file_name" /></td>
        		</tr>
        		<tr>
        			<td><label for="category">Category:</label></td>
        			<td><input type="text" name="category" /></td>
        		</tr>
        		<tr>
        			<td><label for="req_type">Request Type:</label></td>
        			<td><input type="text" name="req_type" /></td>
        		</tr>
        		<tr>
        			<td><label for="text_file">Local File:</label></td>
        			<td><input type="file" name="text_file" /></td>
        		</tr>
        		<tr>
        			<td colspan=2><input type="submit" value="Submit" /></td>
        		</tr>
            </table>
        </form>
    </body>
</html>
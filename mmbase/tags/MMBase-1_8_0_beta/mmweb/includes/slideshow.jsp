<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
        "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<mm:cloud>
<mm:import externid="image" />
<html>
<mm:node number="$image" notfound="skip">
<head>
  <title><mm:field name="title" /></title>
  <link rel="stylesheet" type="text/css" href="<mm:url page="/css/mmmbase.css" />" />
  <meta http-equiv="imagetoolbar" content="no" />
</head>
<body>
<div id="windowopen">
<a href="#" onClick="window.close()" title="Click on image to close window"
><img src="<mm:image template="s(600)" />" width="600" border="0" /></a>
<mm:field name="title" jspvar="images_title" vartype="String" write="false">
<% if(!images_title.equals("")&&images_title.indexOf("#NZ#")==-1) { %>
	<h5><%= images_title %></h5>
<% } %>
</mm:field>
<p><mm:field name="description" />
<span class="close"><a href="javascript:window.close();">close this window</a></span></p>
</div>
</mm:node>
</body>
</html>
</mm:cloud>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<html>
<head>
<title>Generic MMBase admin pages</title>
<link rel="stylesheet" type="text/css" href="../css/mmbase-dev.css">
</head>

<body>
<h1><%=org.mmbase.Version.get() %></h1>

<ul>
  <li><a href="<mm:url page="/editors" />">Editwizards</a></li>
  <li><a href="<mm:url page="edit/" />">Generic editors</a></li>
  <li><a href="<mm:url page="edit/my_editors/" />">Generic editors ('my editors')</a></li>
  <li><a href="<mm:url page="mmeditors/" />">Generic editors ('scan-like editors')</a></li>
  <li><a href="<mm:url page="admin/" />">Admin pages</a></li>
  <li><a href="<mm:url page="security/" />">Security administration</a></li>
</ul>

</body>
</html>
</mm:content>
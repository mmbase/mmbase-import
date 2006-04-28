<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=utf-8" session="false"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<html>
  <head>
    <title>Welcome to MMBase</title>
    <link rel="stylesheet" href="<mm:url page="/mmbase/style/css/mmbase.css" />" type="text/css" />
    <link rel="icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:url page="/mmbase/style/images/favicon.ico" />" type="image/x-icon" />

  </head>
<body  class="basic">
<h1><%=org.mmbase.Version.get() %></h1>
<ul>
  <li><a href="edit">Generic editors</a></li>
  <li><a href="admin">Admin pages</a></li>
  <!-- li><a href="security">Security administration</a></li-->
</ul>
</body>
</html>
</mm:content>

<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<?xml version="1.0" encoding="UTF-8"?>
<html>
  <META HTTP-EQUIV="refresh" content="0; url=<mm:url page="entrancepage.jsp" />" />
  <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
  <title>Logout</title>
</head>
<body>
<mm:cloud method="logout"  jspvar="cloud" />
<body class="basic"><% 
request.getSession().invalidate(); // start all over again %>
<h2>You were logged out. </h2>
<hr />
</body>



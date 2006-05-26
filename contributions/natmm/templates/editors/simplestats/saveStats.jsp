<%@include file="../../includes/templateheader.jsp" %>
<%@ page import="java.util.*" %>
<mm:cloud>
<html>
<head><link rel="stylesheet" type="text/css" href="/css/website.css"></head>
<body>

Saving stats ...<br>
<%@include file="saveToday.jsp" %>
pageCount = <%= pageCounter %><br>
<% HashSet visitorsSessions = (HashSet) application.getAttribute("visitorsSessions"); %>
visitorsSessions = <%= visitorsSessions %><br>
visitorsCount = <%= visitorsCounter %><br><br>
<%	pageCounter = new Hashtable();
	visitorsSessions = new HashSet();
	visitorsCounter = new Integer(0);
	application.setAttribute("pageCounter", pageCounter);
	application.setAttribute("visitorsSessions", visitorsSessions);
	application.setAttribute("visitorsCounter", visitorsCounter);	
%>
ready.<br>
pageCount = <%= pageCounter %><br>
visitorsSessions = <%= visitorsSessions %><br>
visitorsCount = <%= visitorsCounter %><br>
</body>
</mm:cloud>

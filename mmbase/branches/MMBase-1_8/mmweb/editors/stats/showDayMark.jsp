<%@include file="../../includes/templateheader.jsp" %>
<%@ page import="java.util.*" %>
<html>
<head><link rel="stylesheet" type="text/css" href="/css/website.css"></head>
<body>

<%	Calendar cal = Calendar.getInstance();
	Date dd = new Date();
	cal.setTime(dd);
%>
<table>
<tr><td>Date</td><td>Mark</td><td>Daycount</td><td>Number</td></tr>
<mm:list path="daymarks" orderby="daymarks.number" directions="DOWN">
	<tr><td><%= cal.get(Calendar.DAY_OF_MONTH) %>/<%= cal.get(Calendar.MONTH) %></td><td><mm:field name="daymarks.mark" /></td><td><mm:field name="daymarks.daycount" /></td><td><mm:field name="daymarks.number" /></td></tr>
	<% cal.add(Calendar.DATE,-1); %>
</mm:list>
</table>
</body>
<%@include file="../../includes/templatefooter.jsp" %>

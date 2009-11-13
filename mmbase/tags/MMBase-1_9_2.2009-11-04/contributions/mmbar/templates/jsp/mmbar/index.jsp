<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<HTML>   
<HEAD>
<mm:cloud method="http" rank="administrator">
   <%@ include file="thememanager/loadvars.jsp" %>
   <link rel="stylesheet" type="text/css" href="css/default.css" />
   <TITLE>MMBar</TITLE>
</HEAD>
<mm:import externid="main" >help</mm:import>
<mm:import externid="sub" >none</mm:import>
<mm:import externid="id" >none</mm:import>
<mm:import externid="help" >off</mm:import>


<body onload="doLoad()">
<!-- first the selection part -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="95%">

<tr>

		<th COLSPAN="8">
		 MMBase Barometer for Applications and Resources - version 0.3
		</th>
</tr>
</table>

<%@ include file="headers/main.jsp" %>
<mm:compare referid="help" value="on">
</mm:compare>
<mm:write referid="main">
 <mm:compare value="loading"><%@ include file="loading/index.jsp" %></mm:compare>
 <mm:compare value="reading"><%@ include file="reading/index.jsp" %></mm:compare>
 <mm:compare value="writing"><%@ include file="writing/index.jsp" %></mm:compare>
 <mm:compare value="help"><%@ include file="help/index.jsp" %></mm:compare>
 <mm:compare value="mixed"><%@ include file="mixed/index.jsp" %></mm:compare>
 <mm:compare value="endurance"><%@ include file="endurance/index.jsp" %></mm:compare>
</mm:write>


</mm:cloud>
<br />
<br />
</BODY>
</HTML>

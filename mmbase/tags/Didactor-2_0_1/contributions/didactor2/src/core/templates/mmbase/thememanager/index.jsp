<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<HTML>   
<HEAD>
<mm:cloud>
   <%@ include file="loadvars.jsp" %>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <TITLE>MMBase ThemeManager</TITLE>
</HEAD>
<mm:import externid="main" >assigned</mm:import>
<mm:import externid="sub" >none</mm:import>
<mm:import externid="id" >none</mm:import>
<mm:import externid="help" >on</mm:import>


<body>
<!-- first the selection part -->
<center>
<table cellpadding="0" cellspacing="0" class="list" style="margin-top : 30px;" width="95%">

<tr>

		<th COLSPAN="8">
		 MMBase ThemeManager - version 0.21
		</th>
</tr>
</table>

<%@ include file="headers/main.jsp" %>
<mm:compare referid="help" value="on">
	<%@ include file="help/main.jsp" %>
</mm:compare>
<mm:write referid="main">
 <mm:compare value="assigned"><%@ include file="assigned/index.jsp" %></mm:compare>
 <mm:compare value="themes"><%@ include file="themes/index.jsp" %></mm:compare>
</mm:write>


</mm:cloud>
<br />
<br />
</BODY>
</HTML>

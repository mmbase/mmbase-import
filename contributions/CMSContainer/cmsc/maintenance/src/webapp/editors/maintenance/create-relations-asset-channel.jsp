<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp"
%><%@ page import="com.finalist.cmsc.maintenance.beans.*"
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd">
<html xmlns="http://www.w3.org/TR/xhtml">
<head>
<title>Create relations from assets to channels</title>
<meta http-equiv="pragma" value="no-cache" />
<meta http-equiv="expires" value="0" />
<link href="../style.css" type="text/css" rel="stylesheet"/>
</head>
    <body>
       <h2>Create relations from assets to channels</h2>
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<form method="post">
	<input type="hidden" name="action" value="add"/>
   Parent channel Node Number:<input type="text" name="number"/>
	<input type="submit" name="action" value="Create"/>
</form>

<mm:import externid="action"/>
<mm:import externid="number"/>
<mm:present referid="action">
   <mm:write referid="number" jspvar="number" vartype="Integer">
	<%= new CreateRelationsForSecondaryContent(cloud,number).execute() %>
	</mm:write>
</mm:present>

</mm:log>
</mm:cloud>
   </body>

<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:import externid="language">nl</mm:import><mm:locale language="$language"><mm:cloud jspvar="cloud" loginpage="login.jsp"><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:write referid="language" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.util.media.resources.mediaedit", locale);
%>
</mm:write>

<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <!--

    @author   Michiel Meeuwissen
    @version  $Id: entrancepage.jsp,v 1.14 2003-01-03 21:47:49 michiel Exp $ 
    -->
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<head>
<body>
  <p>
    <ul>
     <li><a href="edit.jsp">Edit</a></li>
     <li><a href="view.jsp">View</a></li>
    </ul>
  </p>
  <p align="right">
    <img src="images/mmbase.png" />
  </p>
</body>
</html>
</mm:cloud>
</mm:locale>
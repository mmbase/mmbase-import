<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:content language="$config.lang" postprocessor="reducespace">
<mm:cloud jspvar="cloud" loginpage="login.jsp">
<html>
<head>
   <title><mm:write id="title" value="<%=m.getString("title")%>" /></title>
   <!--

    @author   Michiel Meeuwissen
    @version  $Id: entrancepage.jsp,v 1.17 2003-09-24 10:07:41 michiel Exp $ 
    -->
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<head>
<body>
  <p>
    <ul>
     <li><a href="<mm:url page="edit.jsp" />">Edit</a></li>
     <li><a href="<mm:url page="search.jsp" />">Search</a></li>
     <li><a target="content" href="<mm:url page="help_${config.lang}.jsp" />">Help</a></li>
    </ul>
  </p>
  <p align="right">
    <img src="images/mmbase.png" />
  </p>
</body>
</html>
</mm:cloud>
</mm:content>
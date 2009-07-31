<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@include file="config/read.jsp" %><mm:content language="$config.lang" postprocessor="reducespace">
<mm:cloud jspvar="cloud">
<html>
<head>
   <title><mm:write id="title" value='<%=m.getString("title")%>' /></title>
   <!--

    @author   Michiel Meeuwissen
    @version  $Id$ 
    -->
   <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<head>
<body class="left">
  <mm:import id="current">none</mm:import>
  <%@include file="submenu.jsp" %>
  <hr />
  <p align="right">
    <img src="images/mmbase.png" />
  </p>
</body>
</html>
</mm:cloud>
</mm:content>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
   <title><mm:write id="title" value="Media edit example" /></title>
   <!--

    @since    MMBase-1.6
    @author   Michiel Meeuwissen
    @version  $Id: index.jsp,v 1.1 2002-06-11 22:25:27 michiel Exp $
 
    -->
</head>
<body>
<form>
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->     
  <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%></mm:import>
  <mm:import id="jsps">/mmapps/editwizard/jsp/</mm:import>
	<h1><mm:write referid="title" /></h1>
  <p>
    needed: mediasource, mediafragments. 'posrel' relation between
    them. 'parent/child' between mediafragments.
  </p>
  <p>
   Fragment editor:
  </p>
  <ul>
	<li><a href="<mm:url referids="referrer" page="${jsps}list.jsp">           
           <mm:param name="wizard">tasks/fragments</mm:param>
          <mm:param name="nodepath">mediafragments</mm:param>
           <mm:param name="fields">title</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Fragments</a></li>
	<li><a href="<mm:url referids="referrer" page="${jsps}list.jsp">           
           <mm:param name="wizard">tasks/sources</mm:param>
          <mm:param name="nodepath">mediasources</mm:param>
           <mm:param name="fields">url,format</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Sources</a></li>
   </ul>
  

</html>

<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@page language="java" contentType="text/html; charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@include file="config/read.jsp" %><mm:content language="$config.lang" type="text/html" expires="0">
<mm:import externid="language"><mm:write value="$config.lang" /></mm:import>

<mm:import externid="parameters" />
<mm:import externid="url">index_users.jsp</mm:import>
<mm:import externid="location">/mmbase/security/</mm:import>
<mm:import externid="visibleoperations">read,write,delete,change context</mm:import>

<mm:import id="thisdir"><%=new  java.io.File(request.getServletPath()).getParentFile()%></mm:import>    

<html>
  <head>
    <title>Cloud Context Users Administration</title>
    <link rel="icon" href="images/favicon.ico" type="image/x-icon" />
    <link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
    <style>
      @import url(<mm:url page="${location}style/default.css" />);
      @import url(style/streammanager.css);
    </style>
  </head>
  <body>
    <mm:notpresent referid="parameters">
      <mm:import id="thisparameters">location,language,visibleoperations</mm:import>
      <mm:include referids="thisparameters@parameters,$thisparameters" page="${location}${url}" />
    </mm:notpresent>
    <mm:present referid="parameters">
      <mm:include page="${location}${url}" />
    </mm:present>
  </body>
</html>
</mm:content>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "DTD/xhtml1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@page language="java" contentType="text/html; charset=UTF-8"
%><mm:import externid="language">en</mm:import>

<%@include file="settings.jsp"%>
<mm:import externid="url">index_users.jsp</mm:import>
<mm:import externid="location" />
<mm:import externid="parameters">location,language</mm:import>

<html>
<head>
    <title>Cloud Context Users Administration</title>
    <link href="<mm:write referid="stylesheet" />" rel="stylesheet" type="text/css" />
</head>
<body>
  <mm:notpresent referid="location">
    <mm:include referids="parameters,$parameters" page="${location}${url}" />
  </mm:notpresent>
  <mm:present referid="location">
    <mm:include page="${location}${url}" />
  </mm:present>
</body>
</html>


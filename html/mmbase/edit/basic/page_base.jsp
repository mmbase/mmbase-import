<%@page errorPage="error.jsp" language="java" contentType="text/html; charset=utf-8" 
%><?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<% response.setContentType("text/html; charset=utf-8");
// as many browsers as possible should not cache:
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma","no-cache");
long now = System.currentTimeMillis();
response.setDateHeader("Expires",  now);
response.setDateHeader("Last-Modified",  now);
response.setDateHeader("Date",  now);
%><html>
<head>
<link rel="icon" href="images/favicon.ico" type="image/x-icon" />
<link rel="shortcut icon" href="images/favicon.ico" type="image/x-icon" />
<%@ page import="org.mmbase.bridge.*"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%>


<mm:import id="config" externid="mmeditors_config" from="session" />

<mm:context id="config" referid="config">
  <mm:import externid="page_size" from="parameters,this">20</mm:import>
  <mm:import id="style_sheet" externid="mmjspeditors_style"     from="parameters,cookie,this">mmbase.css</mm:import>
  <mm:import id="liststyle"   externid="mmjspeditors_liststyle" from="parameters,cookie,this">short</mm:import>  
  <mm:import id="lang"        externid="mmjspeditors_language"  from="parameters,cookie,this" ><%=LocalContext.getCloudContext().getDefaultLocale().getLanguage()%></mm:import>
  <mm:import id="method"      externid="mmjspeditors_method"    from="parameters,cookie,this" >loginpage</mm:import>
  <mm:import id="session"     externid="mmjspeditors_session"   from="parameters,cookie,this">mmbase_editors_cloud</mm:import>
</mm:context>

<mm:write referid="config" session="mmeditors_config" />

<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:write referid="config.lang" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.applications.jsp.editors.editors", locale);
%>
</mm:write>
<mm:import id="style"><style type="text/css">@import url(css/<mm:write referid="config.style_sheet" />);</style></mm:import>
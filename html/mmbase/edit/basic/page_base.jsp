<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<% response.setContentType("text/html; charset=utf-8");
// as many browsers as possible should not cache:
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma","no-cache");
long now = System.currentTimeMillis();
response.setDateHeader("Expires",  now);
response.setDateHeader("Last-Modified",  now);
response.setDateHeader("Date",  now);
%><%@ page language="java" contentType="text/html; charset=utf-8"
%><html>
<head>
<%@ page import="org.mmbase.bridge.*"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm"
%>

<!-- some configuration -->

<!-- first try if it is in the session -->
<mm:import id="config" externid="mmeditors_config" from="session" />

<!-- if not, fill it with default -->
<mm:notpresent referid="config">
<mm:remove referid="config" /><!-- it is not possible to overwrite existing var -->
<mm:context id="config">
  <mm:import id="page_size">20</mm:import>
  <mm:import id="hide_search">false</mm:import>
  <mm:import id="style_sheet" externid="mmjspeditors_style" from="cookie">mmbase.css</mm:import>
  <mm:import id="lang"        externid="mmjspeditors_language"  from="cookie" >en</mm:import>
  <mm:import id="method"        externid="mmjspeditors_method"  from="cookie" >loginpage</mm:import>
  <mm:import id="session"       externid="mmjspeditors_session"  from="cookie" >mmbase_editors_cloud</mm:import>
</mm:context>
<mm:write referid="config" session="mmeditors_config" />
</mm:notpresent>

<% java.util.ResourceBundle m = null; // short var-name because we'll need it all over the place
   java.util.Locale locale = null; %>
<mm:write referid="config.lang" jspvar="lang" vartype="string">
<%
  locale  =  new java.util.Locale(lang, "");
  m = java.util.ResourceBundle.getBundle("org.mmbase.applications.jsp.editors.editors",
                locale);
%>
</mm:write>

<mm:import id="style"><style type="text/css">@import url(css/<mm:write referid="config.style_sheet" />);</style></mm:import>

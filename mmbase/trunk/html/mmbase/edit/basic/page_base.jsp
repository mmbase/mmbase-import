<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd"> 
<% response.setContentType("text/html; charset=utf-8"); 
// as many browsers as possible should not cache:
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma","no-cache");
String now = org.mmbase.util.RFC1123.makeDate(new java.util.Date());
response.setHeader("Expires",  now); 
response.setHeader("Last-Modified",  now); 
response.setHeader("Date",  now); 
%><%@ page errorPage="error.jsp" language="java" contentType="text/html; charset=utf-8"
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
  <mm:import id="style_sheet">mmbase.css</mm:import>
</mm:context>
<mm:write referid="config" session="mmeditors_config" />
</mm:notpresent>

<mm:import id="style"><link href="css/<mm:write referid="config.style_sheet" />" rel="stylesheet" type="text/css" media="screen"  /></mm:import>
<mm:import id="SESSION">mmbase_editors_cloud</mm:import>

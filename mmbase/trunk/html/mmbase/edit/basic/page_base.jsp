<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd"> 
<% response.setHeader("Content-Type", "text/html; charset=utf-8"); 
// as many browsers as possible should not cache:
response.setHeader("Cache-Control", "no-cache");
response.setHeader("Pragma","no-cache");
response.setHeader("Expires",  "0"); 
%><%@ page errorPage="error.jsp" %>
<html>
<head> 
<%@ page import="org.mmbase.bridge.*"    
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"  prefix="mm" 
%>
<!-- some configuration -->
<mm:context id="config">
<mm:import id="page_size">20</mm:import>
</mm:context>

<mm:import id="style"><link href="css/mmbase.css" rel="stylesheet" type="text/css" media="screen" /></mm:import>
<mm:import id="SESSION">mmbase_editors_cloud</mm:import>

<%@page language="java" contentType="text/html;charset=utf-8"
%><% response.setContentType("text/html; charset=UTF-8"); 
%><%@include file="/taglibs.jsp" 
%><%
response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
response.setHeader("Pragma","no-cache"); //HTTP 1.0
response.setDateHeader ("Expires", 0); //prevents caching at the proxy server 
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%= javax.servlet.http.HttpUtils.getRequestURL(request) %>" />
<meta name="robots" content="index,follow" />
<meta http-equiv="imagetoolbar" content="no" />
<meta http-equiv="pragma" content="no-cache">
<meta http-equiv="expires" content="-1">
<%@include file="../includes/time.jsp" %>
<%@include file="../includes/request_parameters.jsp" %>
<%@page language="java" contentType="text/html; charset=utf-8"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@taglib uri="http://www.opensymphony.com/oscache" prefix="cache" 
%><%
String portalId = "natuurherstel"; 
String websiteId = request.getParameter("w"); 
String pageId = request.getParameter("p"); 
String articleId = request.getParameter("a");
String imageId = request.getParameter("i"); 
String offsetId = request.getParameter("o");
boolean isPreview = true;
String imageTemplate = "";
%>

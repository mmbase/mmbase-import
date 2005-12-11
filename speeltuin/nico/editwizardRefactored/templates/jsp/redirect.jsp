<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@page import="org.mmbase.applications.editwizard.Controller"
%><%
//get redirect uri
String uri = (String)request.getAttribute(Controller.ATTRKEY_REDIRECT_URI);
//redirect
response.sendRedirect(uri);
%>
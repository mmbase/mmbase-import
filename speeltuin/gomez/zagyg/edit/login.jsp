<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.1 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1.1-strict.dtd">
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0"   prefix="mm"
%><%@page language="java" contentType="text/html;charset=utf-8"
%><%@ include file="util/nocache.jsp"
%><mm:import externid="language">nl</mm:import>
<mm:content language="$language" postprocessor="reducespace" expires="1"><html>
  <head>
    <title>Logon</title>
    <link rel="icon" href="<mm:url id="favi" page="images/edit.ico" />" type="image/x-icon" />
    <link rel="shortcut icon" href="<mm:write referid="favi" />" type="image/x-icon" />
    <link rel="stylesheet" href="css/edit.css" />
  </head>
    <mm:import id="dir">../</mm:import>
    <%@include file="util/login.jsp" %>
  </body>
</html>
</mm:content>
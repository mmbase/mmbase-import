<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html><jsp:directive.include file="page_base_functionality.jsp"
/><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0" jspvar="locale">
<mm:formatter xslt="xslt/framework/head.xslt" escape="none">
  <head>
    <title>Login</title>
    <jsp:directive.include file="head.entries.jsp" />
  </head>
</mm:formatter>
<body class="basic">
  <h2>Login: <span class="uri"><mm:escape>${config.uri}</mm:escape></span></h2>

  <mm:cloud sessionname="$config.session" method="logout" uri="$config.uri"/>

  <mm:include attributes="config.lang@language, config.country@country, config.session@sessionname"  page="login.p.jsp" />
  <%@ include file="footfoot.jsp"  %>
</mm:content>

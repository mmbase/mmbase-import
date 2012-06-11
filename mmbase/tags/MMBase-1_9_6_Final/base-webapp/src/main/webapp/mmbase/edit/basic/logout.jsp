<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "DTD/xhtml1-transitional.dtd">
<html><jsp:directive.include file="page_base_functionality.jsp"
/><mm:content type="text/html" language="$config.lang" country="$config.country" expires="0">
<%--  java.util.Collections.list(session.getAttributeNames()) --%>
<mm:cloud sessionname="$config.session" method="asis">
  <mm:cloudinfo id="userlogon" type="user" write="false" />
</mm:cloud>
<mm:notpresent referid="userlogon">
  <mm:import id="userlogon">--</mm:import>
</mm:notpresent>

<mm:compare referid="userlogon" value="anonymous">
  <mm:remove referid="userlogon" />
  <mm:import id="userlogon" />
</mm:compare>

<mm:cloud method="logout"  sessionname="$config.session" />
<mm:formatter xslt="xslt/framework/head.xslt" escape="none">
  <head>
    <title>Logging out</title>
    <jsp:directive.include file="head.entries.jsp" />
  </head>
</mm:formatter>
<body class="basic"><%
//request.getSession().invalidate(); // start all over again %>
<h2>You were logged out. </h2>
<hr />
<form action="<mm:url page="search_node.jsp" />">
  <input type="submit" name="back" value="back to editors" />
  As: <input type="text" name="userlogon" value="<mm:write referid="userlogon" />" />
</form>
<%@ include file="footfoot.jsp"  %>
</mm:content>
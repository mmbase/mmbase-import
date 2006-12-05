<%-- !DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml/DTD/transitional.dtd" --%>
<%@ page contentType="text/html; charset=utf-8" language="java" %>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud logon="admin" pwd="<%= (String) com.finalist.mmbase.util.CloudFactory.getAdminUserCredentials().get("password") %>" method="pagelogon" jspvar="cloud">
<%@ include file="thememanager/loadvars.jsp" %>
<%@ include file="settings.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="<mm:write referid="style_default" />" />
   <title>MMBob</title>
</HEAD>
<mm:import externid="forumid" />
<mm:node number="$forumid">
   <mm:relatednodescontainer type="posters">
      <mm:constraint field="firstname" value=""/>
      <mm:constraint field="lastname" value=""/>
      <mm:constraint field="email" value=""/>
      <mm:relatednodes>
         <mm:deletenode deleterelations="true" />
      </mm:relatednodes>
   </mm:relatednodescontainer>
</mm:node>
</mm:cloud>


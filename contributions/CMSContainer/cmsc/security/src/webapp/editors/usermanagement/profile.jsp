<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
  <title><fmt:message key="profile.title" /></title>
  <link rel="stylesheet" type="text/css" href="../style.css" />
</head>
<body>
<mm:cloud jspvar="cloud" loginpage="../login.jsp">

	<h1><fmt:message key="profile.title" /></h1>

	<a href="changepassword.jsp"><fmt:message key="changepassword.title" /></a>

<%--
    <table class="listcontent">
       <tr>
          <td><mm:fieldinfo type="guiname" /></td>
          <td><mm:fieldinfo type="guivalue" /></td>
       </tr>
       </mm:field> 
       <mm:fieldlist nodetype="contentelement" type="edit">
       <tr>
          <td><mm:fieldinfo type="guiname" /></td>
          <td><mm:fieldinfo type="guivalue" /></td>
       </tr>
       </mm:fieldlist> 
    </table>
--%>
</mm:cloud>
</body>
</html:html>
</mm:content>
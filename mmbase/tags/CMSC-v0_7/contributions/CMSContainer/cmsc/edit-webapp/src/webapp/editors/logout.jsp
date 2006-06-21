<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:cloud method="logout">
</mm:cloud>
<% request.getSession().invalidate(); %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
   <title><fmt:message key="logout.title" /></title>
<meta http-equiv="refresh" content="0; url=index.jsp" />
   <style type="text/css">
      body.login {
         font-family: Verdana, serif;
         margin: 100px;
         text-align: center;
      }
      body.login #Content {
         border: 1px solid blue;
         margin: 0px auto;
         text-align: left;
         width: 276px;
      }
      div #Inner {
         margin: 10px 5px 5px 5px;
      }
      tr.inputrow td {
         padding: 2px 1px 10px 3px;
      }
      td.version {
         font-size: 10px;
      }
   </style>
</head>
<body class="login">
<div id="Content">
	<fmt:message key="logout.message" /><br />
	<a href="index.jsp"><fmt:message key="logout.link" /></a>
</div>
</body>
</html:html>
</mm:content>
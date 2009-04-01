<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@include file="globals.jsp" 
%><!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<cmscedit:head title="logout.title">
   <style type="text/css">
      body {
         margin: 100px auto 0 auto;
         text-align: center;
         width: 285px;
      }
   </style>
</cmscedit:head>
<body>
	<cmscedit:sideblock title="logout.title">
         <fmt:message key="logout.message" />
         <br />
         <a href="index.jsp"><fmt:message key="logout.link" /></a>
    </cmscedit:sideblock>
</body>
</html:html>
<mm:cloud method="logout">
</mm:cloud>
<% request.getSession().invalidate(); %>
<% request.getSession().setAttribute("logout", true);%>
</mm:content>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:cloud method="logout">
</mm:cloud>
<% request.getSession().invalidate(); %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
   <title><fmt:message key="logout.title" /></title>
   <link rel="stylesheet" type="text/css" href="<cmsc:staticurl page='/editors/css/main.css'/>" />
   <style type="text/css">
      body {
         margin: 100px;
         text-align: center;
      }
      div.side_block, div.side_block table {
         position: relative;
         margin: 0px auto;
      }
   </style>
</head>
<body>
   <div class="side_block">
      <div class="header">
         <div class="title"><fmt:message key="logout.title" /></div>
         <div class="header_end"></div>
      </div>
      <div class="body">
         <fmt:message key="logout.message" />
         <br />
         <a href="index.jsp"><fmt:message key="logout.link" /></a>
      </div>
      <div class="side_block_end"></div>
   </div>
</body>
</html:html>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="logout.title">
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
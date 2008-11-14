<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="newsletter" scope="request" />

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="selector.newsletterpublication">
   <style type="text/css">
   input { width: 100px;}
   </style>
   <script src="content.js" type="text/javascript"></script>
   <script>
     refreshFrame('pages');
   </script>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="selector.newsletterpublications" titleClass="side_block_green">
      <p style="margin-left:10px">
          <fmt:message key="newsletter.edition.success" >
             <fmt:param value="${action}"/>
          </fmt:message>
      </p>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>
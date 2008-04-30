<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="newsletter" scope="request" />

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="sendsuccess.title">
	<style type="text/css">
	input { width: 100px;}
	</style>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="sendsuccess.title" titleClass="side_block_green">
   <c:if test="${not empty isPaused}">
      <p> <fmt:message key="newsletter.ispaused" />
	   </p>
   </c:if>
   <c:if test="${empty isPaused}">
	   <p>
		   <fmt:message key="sendsuccess.subtitle" />
	   </p>
   </c:if>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>
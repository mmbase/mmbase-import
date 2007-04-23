<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<fmt:setBundle basename="cmsc-utils" scope="request" />
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="cleanicaches.title" /></title>
	</head>
	<body>
<mm:cloud jspvar="cloud" rank="basic user" method="http">

	<mm:listnodes type="icaches">
		<mm:deletenode />
	</mm:listnodes>

</mm:cloud>
	</body>
</html:html>
</mm:content>
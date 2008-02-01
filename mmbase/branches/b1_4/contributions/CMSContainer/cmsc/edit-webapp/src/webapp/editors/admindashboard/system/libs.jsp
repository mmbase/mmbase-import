<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="admindashboard.title">
	<link href="../../css/compact.css" type="text/css" rel="stylesheet" />
</cmscedit:head>
<body>
<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
	<mm:hasrank minvalue="administrator">
	
		<h1><fmt:message key="admindashboard.system.libs.header" /></h1>
		
		<b><fmt:message key="admindashboard.system.libs.number" />:</b>
		<cmsc:version type="libs" var="libs"/>
		${fn:length(libs)}
		<br/>
		<br/>
		<a href="viewlibs.jsp" target="_parent"><fmt:message key="admindashboard.system.libs.viewall" /></a>
		
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>

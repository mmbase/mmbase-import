<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title><fmt:message key="channeldelete.title" /></title>
		<link href="../style.css" type="text/css" rel="stylesheet" />
		<style type="text/css">
			input { width: 100px;}
		</style>
	</head>
	<mm:import externid="number" required="true" from="parameters" />
	<mm:cloud jspvar="cloud" rank="basic user" method='http'>
		<body>
		<h2><fmt:message key="channeldelete.title" /></h2>
		<h3><fmt:message key="channeldelete.subtitle" /> <mm:node
			referid="number">
			<mm:field name="name" />
		</mm:node></h3>
		<fmt:message key="channeldelete.confirm" />
		<form action="?">
			<input type="hidden" name="number" value="<mm:write referid="number"/>" /> 
			<input type="submit" name="remove" value="<fmt:message key="channeldelete.yes" />" />
		</form>
		</body>
	</mm:cloud>
	</html:html>
</mm:content>

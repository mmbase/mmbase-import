<%@page language="java" contentType="text/html;charset=utf-8" %>
<%@include file="globals.jsp"  %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<link rel="stylesheet" type="text/css" href="../css/main.css" />
<title><fmt:message key="changelanguage.title" /></title>
</head>


<script>
	function messageAndRefresh() {
		alert('<fmt:message key="changelanguage.succeeded" />')
		top.location.href = "../index.jsp";
	}
</script>
      <body onload="messageAndRefresh()">
      </body>
</html:html>
</mm:content>
<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>
<fmt:setBundle basename="cmsc-utils" scope="request" />
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<head>
		<title>Clean Remote Nodes</title>
	</head>
	<body>
<mm:cloud jspvar="cloud" rank="administrator" method="http">
   Cleaning remote nodes:<br>
	<mm:listnodes type="remotenodes">
	  <mm:first>Number of remote nodes: <mm:size/><hr/></mm:first>
	  <mm:deletenode />
	</mm:listnodes>
</mm:cloud>
Done<br>
	</body>
</html:html>

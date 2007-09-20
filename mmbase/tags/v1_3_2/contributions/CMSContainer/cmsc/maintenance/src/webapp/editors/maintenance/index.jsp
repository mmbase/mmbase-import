<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<mm:cloud jspvar="cloud" loginpage="../login.jsp">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">
	<head><title><fmt:message key="maintenance.title" /></title>
	</head>
	<frameset cols="321,*" framespacing="0" frameborder="0">
		<frame src="menu.jsp" name="leftpane" frameborder="0" scrolling="no">
		<frame src="../empty.html" name="rightpane" frameborder="0">
	</frameset>
</html:html>
</mm:cloud>
</mm:content>
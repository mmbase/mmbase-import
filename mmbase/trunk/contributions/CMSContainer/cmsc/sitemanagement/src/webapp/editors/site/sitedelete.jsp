<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="globals.jsp" %>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="sitedelete.title">
	<style type="text/css">
	input { width: 100px;}
	</style>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:cloud jspvar="cloud" rank="administrator" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="sitedelete.title" titleClass="side_block_green">
	<p>
		<fmt:message key="sitedelete.subtitle" /> <mm:node referid="number"><b><mm:field name="title"/></b></mm:node>
		<br/>
		<br/>
		<fmt:message key="sitedelete.confirm" />
		<br/>
		<form action="?">
	   	<input type="hidden" name="number" value="<mm:write referid="number"/>" />
	   	<input type="submit" name="remove" value="<fmt:message key="sitedelete.yes" />"/>&nbsp;
	    <input type="submit" name="cancel" value="<fmt:message key="sitedelete.no" />"/>
		</form>
	</p>
</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>
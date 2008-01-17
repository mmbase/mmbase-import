<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="newsletter" scope="request" />

<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="testinput.title">
	<style type="text/css">
	input { width: 100px;}
	</style>
</cmscedit:head>
<mm:import externid="number" required="true" from="parameters"/>
<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">
<body>
<cmscedit:sideblock title="testinput.title" titleClass="side_block_green">
	<p>
		<fmt:message key="testinput.subtitle" />		
	</p>
	<form action="?">
		<html:hidden property="number" value="${number}" />
		<html:text property="email" />
	   	<html:submit property="test"><fmt:message key="testinput.send"/></html:submit>&nbsp;
	   	<html:submit property="cancel"><fmt:message key="testinput.cancel"/></html:submit>
	</form>

</cmscedit:sideblock>
</body>
</mm:cloud>
</html:html>
</mm:content>
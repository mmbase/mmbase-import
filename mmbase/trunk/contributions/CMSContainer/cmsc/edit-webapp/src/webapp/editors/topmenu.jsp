<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:import externid="bottomurl" from="parameters"/>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<head>
	<link href="topmenu.css" type="text/css" rel="stylesheet"/>
	<title><fmt:message key="topmenu.title" /></title>
	<script type="text/javascript" src="topmenu.js"></script>
</head>
<body class="topbody" onload="initMenu();">
<mm:cloud jspvar='cloud' rank='basic user'>
	<mm:cloudinfo type="user" id="cloudusername" write="false" />
	<mm:listcontainer path="user">
		<mm:constraint field="user.username" operator="EQUAL" referid="cloudusername" />
	<mm:list>
		<mm:import id="fullname" jspvar="fullname"><mm:field name="user.firstname"/> <mm:field name="user.prefix"/> <mm:field name="user.surname"/></mm:import>
	<div class="topmenu_userinfo">
		<fmt:message key="topmenu.user.title" />: 
		<% if ("".equals(fullname.trim())) { %>
			<mm:write referid="cloudusername"/>
		<% } else { %>	
			<mm:write referid="fullname"/>
		<% } %>
    <mm:haspage page="/editors/help/">
		| <a href="help/" target="bottompane" id="tutorial"><fmt:message key="topmenu.help" /></a>
	</mm:haspage>
    <mm:haspage page="/editors/logout.jsp">
		| <a href="logout.jsp" target="_top" id="logout"><fmt:message key="topmenu.logout" /></a>
	</mm:haspage>
	</div>
</mm:list>
</mm:listcontainer>

<div id="cnav">
	<div id="home" >
		<span>
			<a href="dashboard.jsp" target="bottompane" class='topmenu' onclick="selectMenu('home')"><fmt:message key="topmenu.home" /></a>
		</span>
	</div>
    <mm:haspage page="/editors/workflow/">
			<div id="workflow">
				<span>
					<a href="workflow/workflow.jsp" target="bottompane" class='topmenu' onclick="selectMenu('workflow')"><fmt:message key="topmenu.workflow" /></a>
				</span>
			</div>
	</mm:haspage>
    <mm:haspage page="/editors/site/">
			<div id="site">
				<span>
					<a href="site/index.jsp" target="bottompane" class='topmenu' onclick="selectMenu('site', true)"><fmt:message key="topmenu.site" /></a>
				</span>
			</div>
	</mm:haspage>
    <mm:haspage page="/editors/repository/">
			<div id="repository">
				<span>
					<a href="repository/index.jsp" target="bottompane" class='topmenu' onclick="selectMenu('repository')"><fmt:message key="topmenu.repository" /></a>
				</span>
			</div>
	</mm:haspage>
	<div id="profile">
		<span>
			<a href="usermanagement/profile.jsp" target="bottompane" class='topmenu' onclick="selectMenu('profile')"><fmt:message key="topmenu.profile" /></a>
		</span>
	</div>
</div>
</mm:cloud>
</body>
</html:html>
</mm:content>
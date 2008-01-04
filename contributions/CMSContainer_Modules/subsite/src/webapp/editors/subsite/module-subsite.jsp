<%@page language="java" contentType="text/html;charset=utf-8"
%><%@include file="globals.jsp"
%><%@taglib uri="http://www.mmbase.org/mmbase-taglib-2.0" prefix="mm"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<html:html xhtml="true">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">

<cmscedit:head title="subsite.module.title"/>

<body>
<h3><fmt:message key="subsite.module.title" /></h3>

<mm:cloud>
	<mm:listnodes type="subsite">
	   <mm:field name="title"/> at path <mm:field name="path"/><br>
		
		${_node.number} has title: ${_node.title}<br>
		<mm:field name="number" write="false" id="personalpagemember"/>
		<mm:relatednodes type="personalpage" role="navrel" searchdir="destination">
		  - <mm:field name="title"></mm:field><br>
		</mm:relatednodes>
<%--
		<cmsc:list-pages var="children" origin="${personalpagemember}" mode="all"/>
		<c:forEach var="personalpage" items="${children}">
		   $(personalpage.title)<br>
		</c:forEach>  
--%>

	</mm:listnodes>
</mm:cloud>
</body>
</html:html>
</mm:content>
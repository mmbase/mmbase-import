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
	
		<h1><fmt:message key="admindashboard.system.memory.header" /></h1>
		
		<% 
		long maxMem = Runtime.getRuntime().maxMemory();
		long totalMem = Runtime.getRuntime().totalMemory();
		long freeMem = Runtime.getRuntime().freeMemory();
		
		 %>
		
		<b><fmt:message key="admindashboard.system.memory.maximum" />:</b> 
		<%=maxMem/1024/1024%><fmt:message key="admindashboard.system.memory.mb" />
		<br/>
		<b><fmt:message key="admindashboard.system.memory.total" />:</b> 
		<%=totalMem/1024/1024%><fmt:message key="admindashboard.system.memory.mb" />
		<br/>
		<b><fmt:message key="admindashboard.system.memory.used" />:</b> 
		<%=(totalMem - freeMem)/1024/1024%><fmt:message key="admindashboard.system.memory.mb" />
		<br/>
		<b><fmt:message key="admindashboard.system.memory.free" />:</b> 
		<%=freeMem/1024/1024%><fmt:message key="admindashboard.system.memory.mb" />
		
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>

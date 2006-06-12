<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp" %>
<mm:import externid="bottomurl" from="parameters">dashboard.jsp</mm:import>
<mm:locale language="client">
<mm:cloud loginpage="login.jsp" rank="basic user">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Frameset//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-frameset.dtd">
<html:html xhtml="true">
	<head><title><fmt:message key="editors.title" /></title>
	
	<script type="text/javascript" src="editors.js"></script>
	
	</head>
	<frameset rows="35,*" frameborder="NO" framespacing="0" border="0">
		<frame src="topmenu.jsp?bottomurl=<mm:write referid="bottomurl"/>" name="toppane" frameborder="0" scrolling="no" />
		<frame src="<mm:write referid="bottomurl"/>" name="bottompane" frameborder="0" scrolling="yes" />
	</frameset>
</html:html>
</mm:cloud>
</mm:locale>
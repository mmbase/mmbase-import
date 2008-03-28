<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
<cmscedit:head title="admindashboard.title" />
<body>
<mm:cloud jspvar="cloud" loginpage="../login.jsp">
	<mm:hasrank minvalue="administrator">
	    <%@include file="system/index.jsp" %>
		<mm:haspage page="/editors/admindashboard/publish">
	 	   <jsp:include page="publish/index.jsp" />
		</mm:haspage>
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>
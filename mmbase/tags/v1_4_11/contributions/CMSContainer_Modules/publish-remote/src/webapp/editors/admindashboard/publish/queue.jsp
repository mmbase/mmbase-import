<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<cmscedit:head title="publish.title">
		<link href="../../css/compact.css" type="text/css" rel="stylesheet" />
		<script>
			function tryRefresh() {
				if(document.getElementById("refreshBox").checked) {
					forceRefresh();
				}
			}
			function forceRefresh() {
				document.location.href="queue.jsp?refresh=true";
			}
		</script>
	</cmscedit:head>
<body <c:if test="${param.refresh}">onload="setTimeout('tryRefresh()', 5000)"</c:if>>
<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
	<mm:hasrank minvalue="administrator">
	
		<h1><fmt:message key="admindashboard.publish.queue.header" /></h1>
		
		<b><fmt:message key="admindashboard.publish.queue.size" />:</b> 
		<mm:listnodescontainer type="publishqueue">
			<mm:constraint field="status" operator="EQUAL" value="init"/>
			<mm:size/>
		</mm:listnodescontainer>
		<br/>
		<input id="refreshBox" type="checkbox" onclick="tryRefresh()" <c:if test="${param.refresh}">checked</c:if>/> <a href="javascript:forceRefresh()"><fmt:message key="admindashboard.publish.queue.refresh" /></a> 
		<br/>
		<a href="viewqueue.jsp" target="_parent"><fmt:message key="admindashboard.publish.queue.view" /></a>
		
		
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>

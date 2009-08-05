<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<mm:content type="text/html" encoding="UTF-8" expires="0">
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<html:html xhtml="true">
	<mm:cloud jspvar="cloud" loginpage="../../login.jsp">
		<mm:hasrank minvalue="administrator">
	<cmscedit:head title="publish.title">
			<mm:listnodescontainer type="publishqueue">
				<mm:constraint field="status" operator="EQUAL" value="init"/>
				<c:set var="size"><mm:size/></c:set>
			</mm:listnodescontainer>
		
			<link href="../../css/compact.css" type="text/css" rel="stylesheet" />
			<script>
				function tryRefresh() {
					if(document.getElementById("refreshBox").checked) {
						forceRefresh();
					}
				}
				function forceRefresh() {
					document.location.href="queue.jsp?refresh=true&oldsize=${size}";
				}
				function onloadFunction() {
					<c:if test="${!empty param.oldsize && size != param.oldsize}">
						window.parent.published.location.reload();
						window.parent.failed.location.reload();
					</c:if>
					setTimeout('tryRefresh()', 5000);
				}
			</script>
		</cmscedit:head>
	<body <c:if test="${param.refresh}">onload="onloadFunction()"</c:if>>
		<h1><fmt:message key="admindashboard.publish.queue.header" /></h1>
		
		<b><fmt:message key="admindashboard.publish.queue.size" />:</b> ${size}
		<br/>
		<input id="refreshBox" type="checkbox" onclick="tryRefresh()" <c:if test="${param.refresh}">checked</c:if>/> <a href="javascript:forceRefresh()"><fmt:message key="admindashboard.publish.queue.refresh" /></a> 
		<br/>
		<a href="viewqueue.jsp" target="_parent"><fmt:message key="admindashboard.publish.queue.view" /></a>
		
		
	</mm:hasrank>
</mm:cloud>
</body>
</html:html>
</mm:content>

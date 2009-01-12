<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<c:set var="title" scope="request"><fmt:message key="exception.404.message" /></c:set>
<%@include file="header.jsp"%>
<c:choose>
	<c:when test="${fn:indexOf(header.referer,'/Content.do') != -1 || fn:indexOf(header.referer,'/SearchAction.do') != -1}">
		<fmt:message key="exception.404.notlinked.message" /><br />
		<fmt:message key="exception.404.description" /><br />
		<fmt:message key="exception.404.notlinked.actions" /><br />
	</c:when>
	<c:otherwise>
		<fmt:message key="exception.404.message" /><br />
		<fmt:message key="exception.404.description" /><br />
		<fmt:message key="exception.404.actions" /><br />
	</c:otherwise>
</c:choose>
<%@include file="footer.jsp"%>
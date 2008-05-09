<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<c:set var="title" scope="request"><fmt:message key="exception.403.message" /></c:set>
<%@include file="header.jsp"%>
<p>
<fmt:message key="exception.403.message" /><br />
<fmt:message key="exception.403.description" /><br />
<fmt:message key="exception.403.actions" /><br />
</p>
<%@include file="footer.jsp"%>
<%@ include file="globals.jsp"%>

<c:set var="title" scope="request"><fmt:message key="exception.404.message" /></c:set>

<%@include file="header.jsp"%>
<fmt:message key="exception.404.message" /><br />
<fmt:message key="exception.404.description" /><br />
<fmt:message key="exception.404.actions" /><br />

<%@include file="footer.jsp"%>
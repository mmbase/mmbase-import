<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<c:set var="title" scope="request"><fmt:message key="exception.400.message" /></c:set>
<%@include file="header.jsp"%>
<P>
<fmt:message key="exception.400.message" /><br />
<fmt:message key="exception.400.description" /><br />
<fmt:message key="exception.400.actions" /><br />
</P>
<%@include file="footer.jsp"%>
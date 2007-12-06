<%@page language="java" contentType="text/html;charset=utf-8"%>
r
<%@include file="../../globals.jsp" %>

<fmt:setBundle basename="newsletter-module" scope="request" />

<div>
	<h3><fmt:message key="module.index.title" /></h3>
	<%@include file="statistics.jsp" %>
</div>
<br>
<div>
	<%@include file="subscriptionmanagement.jsp" %>
</div>
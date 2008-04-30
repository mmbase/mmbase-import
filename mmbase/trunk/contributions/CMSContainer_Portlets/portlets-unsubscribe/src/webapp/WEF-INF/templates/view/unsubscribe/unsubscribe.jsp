<%@include file="/WEB-INF/templates/portletglobals.jsp" %>
<%@ page import="java.util.*"%>
<% 
String baseUrl = (String)request.getAttribute("baseurl");
%>
<c:set var="url">
<%=baseUrl%>
</c:set>
<br/>
<a href="${url}"><fmt:message key="unsubscribe.link" /></a>
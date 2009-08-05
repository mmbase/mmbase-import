<%@page import="com.finalist.cmsc.mmbase.PropertiesUtil"
%><%@include file="../../globals.jsp"
%><fmt:setBundle basename="newsletter" scope="request"/><c:set var="pagesize"><%=PropertiesUtil.getProperty("repository.search.results.per.page")%></c:set>
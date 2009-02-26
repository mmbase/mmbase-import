<%@ page import="com.finalist.cmsc.mmbase.PropertiesUtil"
%><%@include file="../../globals.jsp"
%><%@page language="java" contentType="text/html;charset=utf-8"
%><fmt:setBundle basename="newsletter" scope="request" /><c:set var="pagesize">
   <%=PropertiesUtil.getProperty("repository.search.results.per.page")%>
</c:set>
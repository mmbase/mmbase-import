<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@include file="../globals.jsp" %>

<fmt:setBundle basename="subsite" scope="request" />

<h3><fmt:message key="view.adduser" /></h3>

<mm:cloud jspvar="cloud" rank="basic user" loginpage="../login.jsp">  

</mm:cloud>
<%@page language="java" contentType="text/html;charset=utf-8"%>
<%@taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<fmt:setBundle basename="cmsc" scope="page" />
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html xmlns="http://www.w3.org/1999/xhtml" xml:lang="nl" lang="nl">
<head><!-- Versie: $Name: not supported by cvs2svn $ -->
<title><%=(request.getAttribute("title") != null)? request.getAttribute("title"): ""%></title>
</head>
<body>
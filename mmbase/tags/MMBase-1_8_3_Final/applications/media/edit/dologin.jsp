<%@page language="java" contentType="text/html;charset=UTF-8" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<?xml version="1.0" encoding="UTF-8"?>
<html>
  <link href="style/streammanager.css" type="text/css" rel="stylesheet" />
<title>Login</title>
</head>
<body>
<mm:cloud loginpage="login.jsp" rank="basic user">
  <jsp:forward page="search.jsp" />
</mm:cloud>

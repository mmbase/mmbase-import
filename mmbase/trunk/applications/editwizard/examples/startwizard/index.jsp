<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html;charset=UTF-8"
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><?xml version="1.0" encoding="UTF-8"?>
<html>
<head>
   <title>EditWizard Examples</title>
   <!--
    Testing startwizards (popups, inlines)
   -->
   <link rel="stylesheet" type="text/css" href="../style.css" />
</head>
<body>
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->
  <mm:import id="referrer"><%=new  java.io.File(request.getServletPath())%></mm:import>
  <mm:import id="jsps">/mmapps/editwizard/jsp/</mm:import>
  <h1>Editwizard Examples (startwizards)</h1>
  <p>
    Testing startwizards.
  </p>
  <p>

        <a href="<mm:url referids="referrer" page="${jsps}list.jsp">           
           <mm:param name="wizard">tasks/people</mm:param>
           <mm:param name="nodepath">people</mm:param>
           <mm:param name="fields">number,firstname,lastname</mm:param>
           <mm:param name="orderby">number</mm:param>
           <mm:param name="directions">down</mm:param>
           </mm:url>">Person test</a>
  </p>
<hr />
   <a href="<mm:url page="../../taglib/showanypage.jsp"><mm:param name="page"><%=request.getServletPath()%></mm:param></mm:url>">Source of this page</a><br />
<a href="<mm:url page="../index.html" />">back</a>

</body>
</html>

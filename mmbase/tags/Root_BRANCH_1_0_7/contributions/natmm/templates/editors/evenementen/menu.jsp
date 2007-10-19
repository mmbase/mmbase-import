<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="/taglibs.jsp" %>
<html>
<head>
   <link rel="stylesheet" type="text/css" href="../css/tree.css">
   <title>Menu beheeromgeving</title>
   <style>
      p {
         margin:3px;
      }
   </style>
</head>
<body style="padding-left:2px;">
   <mm:import externid="action"/>
   <!-- We are going to set the referrer explicitely, because we don't wont to depend on the 'Referer' header (which is not mandatory) -->
   <mm:import externid="language">nl</mm:import>
   <mm:import id="referrer"><%=new java.io.File(request.getServletPath())%>?language=<mm:write  referid="language" /></mm:import>
   <mm:import id="jsps"><%= editwizard_location %>/jsp/</mm:import>
   <mm:import id="debug">false</mm:import>
   <mm:cloud method="http" rank="basic user" jspvar="cloud">
      <%
         boolean isAdmin = cloud.getUser().getRank().equals("administrator");
         boolean isChiefEditor = cloud.getUser().getRank().equals("chiefeditor");
      %>
      <form name="" method="post" action="menu.jsp" />
      <h3>Activiteiten database</h3>
      <mm:import id="startnodes">evenementen_beheer</mm:import>
      <%@include file="../includes/menu_editwizards.jsp" %>
   </mm:cloud>
</body>
</html>

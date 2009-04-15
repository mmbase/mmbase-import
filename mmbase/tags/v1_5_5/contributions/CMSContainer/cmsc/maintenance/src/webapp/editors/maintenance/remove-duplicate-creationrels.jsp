<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="globals.jsp"%>
<%@ page import="com.finalist.cmsc.maintenance.sql.*"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>remover</title>
</head>
    <body>
       <h2>remover</h2>
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="nodetype" />
<form method="post">
	<input type="submit" name="action" value="view"/>
	<input type="submit" name="action" value="remove"/>
</form>

<mm:import externid="action"/>
<mm:present referid="action">
	<mm:write referid="action" jspvar="action" vartype="String">
	<%= new SqlExecutor().execute(new SingleRelationDuplication("creationrel", "DESTINATION", action)) %>
	<%= new SqlExecutor().execute(new SingleRelationDuplication("ownerrel", "DESTINATION", action)) %>
	</mm:write>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>
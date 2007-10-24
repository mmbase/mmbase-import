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
	<select name="nodetype">
	<%
	NodeManagerList l = cloud.getNodeManagers();
	java.util.Collections.sort(l);
	int j = 0;
	for (int i = 0; i < l.size(); i++) {
		NodeManager nm = l.getNodeManager(i); %>
		<option value="<%= nm.getName() %>"><%= nm.getName() + "    (" + nm.getGUIName() + ")" %></option>
	<% } %>
	</select>
	<input type="submit" name="action" value="view"/>
	<input type="submit" name="action" value="remove"/>
</form>

<mm:present referid="nodetype">
	nodetype:	<mm:write referid="nodetype" /><br />
	
	<mm:import externid="action"/>
	<mm:present referid="action">
		<mm:write referid="action" jspvar="action" vartype="String">
		<mm:write referid="nodetype" jspvar="nodetype" vartype="String">
		<%= new SqlExecutor().execute(new OrphanNodes(nodetype, action)) %>
		</mm:write>
		</mm:write>
	</mm:present>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>
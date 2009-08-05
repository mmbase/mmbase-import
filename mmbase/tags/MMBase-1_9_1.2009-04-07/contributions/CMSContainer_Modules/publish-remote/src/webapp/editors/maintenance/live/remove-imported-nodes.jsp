<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../publish-remote/globals.jsp"%>
<%@ page import="com.finalist.cmsc.maintenance.live.*"%>
<%@page import="com.finalist.cmsc.mmbase.TypeUtil"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>remover</title>
</head>
    <body>
       <h2>remover</h2>
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="nodetype" jspvar="nodetype" />
<form method="post">
    <input type="text" name="staging" value="staging.server"/>
	<select name="nodetype">
	<%
	NodeManagerList l = cloud.getNodeManagers();
	java.util.Collections.sort(l);
	int j = 0;
	for (int i = 0; i < l.size(); i++) {
		NodeManager nm = l.getNodeManager(i); 
		if (!TypeUtil.isSystemType(nm.getName(), true)) {
		%>
		<option value="<%= nm.getName() %>" <% if (nm.getName().equals(nodetype)) { %>selected="selected"<% } %> >
			<%= nm.getName() + "    (" + nm.getGUIName() + ")" %>
		</option>
		<%	}
	} %>
	</select>
	<input type="submit" name="action" value="view"/>
	<input type="submit" name="action" value="remove"/>
</form>



<mm:present referid="nodetype">
	nodetype:	<mm:write referid="nodetype" jspvar="nodetype"/><br />

	<mm:import externid="staging" jspvar="staging" />
	<mm:present referid="staging">
		<mm:import externid="action" jspvar="action" />
		<mm:present referid="action">
			<%= new SqlExecutor().execute(new RemoveImportedNodes(nodetype, action)) %>
		</mm:present>
	</mm:present>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>
<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@ page import="org.mmbase.remotepublishing.PublishManager"%>
<%@ page import="org.mmbase.remotepublishing.util.PublishUtil"%>
<%@ page import="org.mmbase.remotepublishing.CloudManager"%>
<%@page import="org.mmbase.bridge.Cloud"%>
<%@page import="org.mmbase.remotepublishing.CloudInfo"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>publisher</title>
</head>
    <body>
       <h2>publisher</h2>
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
	<input type="checkbox" name="publishqueue" value="publishqueue"/> publishqueue
	<input type="checkbox" name="publishall" value="publishall"/> publish all
	<input type="submit" value="publish"/>
</form>

<mm:present referid="nodetype">
	nodetype:	<mm:write referid="nodetype" /><br />
	
	<mm:import externid="publishqueue"/>
	<mm:present referid="publishqueue">
		Using PublishQueue <br />
		<mm:listnodes type="$nodetype" jspvar="node">
			<% PublishUtil.publishOrUpdateNode(node); %>
			<mm:field name="number" /> <br />
		</mm:listnodes>
	</mm:present>
	<mm:notpresent referid="publishqueue">
		NOT Using PublishQueue <br />
	
		<mm:import externid="publishall"/>

		<% 
		CloudInfo localCloudInfo = CloudInfo.getDefaultCloudInfo();
		int remoteCloudNumber = org.mmbase.remotepublishing.builders.PublishingQueueBuilder.getCloudNumber("live.server");
        CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloudNumber);
		%>

		<mm:listnodes type="$nodetype" jspvar="node">
			<%
			if (!PublishManager.isPublished(localCloudInfo,node)) { 
				%><mm:present referid="publishall"><%
					PublishManager.createNodeAndRelations(localCloudInfo, node, remoteCloudInfo);
				%></mm:present><%
			}
			else {
				PublishManager.updateNodesAndRelations(localCloudInfo, node);
			}
			%>
			<mm:field name="number" /> <br />
		</mm:listnodes>
	</mm:notpresent>
published
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>
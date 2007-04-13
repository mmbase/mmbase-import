<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../globals.jsp"%>
<%@ page import="org.mmbase.remotepublishing.PublishManager"%>
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

<mm:import externid="nodenumber"/>
<form method="post">
	<input type="text" name="cloud1" value="live.server"/>
	<input type="text" name="nodenumber" value=""/>
	<input type="submit" value="publish"/>
</form>

<mm:present referid="nodenumber">
<mm:node referid="nodenumber" jspvar="node">
<%
String cloud1 = request.getParameter("cloud1");
if (cloud1 != null && !"".equals(cloud1)) {
    Cloud remoteCloud = CloudManager.getCloud(cloud, cloud1);
    CloudInfo localCloudInfo = CloudInfo.getCloudInfo(cloud);
    CloudInfo remoteCloudInfo = CloudInfo.getCloudInfo(remoteCloud);

	if (!PublishManager.isPublished(localCloudInfo, node)) {
		PublishManager.publishNode(localCloudInfo, node, remoteCloudInfo);
	}
	else {
		PublishManager.updatePublishedNodes(localCloudInfo, node);
	}
}
%>
nodenumber:	<mm:write referid="nodenumber" /><br />
published
</mm:node>
</mm:present>

</mm:log>
</mm:cloud>
      Done!
   </body>
</html>
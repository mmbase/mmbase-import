<%@page language="java" contentType="text/html;charset=UTF-8"%>
<%@include file="../../globals.jsp"%>
<%@page import="com.finalist.cmsc.services.search.*"%>
<%@page import="org.mmbase.bridge.Cloud"%>
<html>
<head>
    <link href="../style.css" type="text/css" rel="stylesheet"/>
    <title>search content</title>
</head>
    <body>
       <h2>search content</h2>
<mm:cloud jspvar="cloud" loginpage="../editors/login.jsp" rank="administrator">
<mm:log jspvar="log">

<mm:import externid="nodenumber"/>
<form method="post">
	<input type="text" name="nodenumber" value=""/>
	<input type="submit" value="search"/>
</form>

<mm:present referid="nodenumber">
nodenumber:	<mm:write referid="nodenumber" /><br />

<mm:node referid="nodenumber" jspvar="node">
<% List<PageInfo> pages = Search.findAllDetailPagesForContent(node);
for (Iterator iterator = pages.iterator(); iterator.hasNext();) {
    PageInfo info = (PageInfo) iterator.next();
%>
<%= "Page = " + info.getPageNumber() + ", host = " + info.getHost() + ", path = " + info.getPath() 
	+ ", window = " + info.getWindowName() + ", parameter = " + info.getParametername() %>
<br />
<% } %>
</mm:node>
</mm:present>

</mm:log>
</mm:cloud>
   </body>
</html>
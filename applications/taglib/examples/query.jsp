<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page errorPage="error.jsp" session="false"  %><mm:content type="text/html">
<html>
<head>
  <title>Queries with MMBase taglib (1.7)</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
<%@ include file="menu.jsp"%>

<h1>Container tags</h1>

<mm:cloud jspvar="cloud">
<table>
<tr><td colspan="2">
  All 4 MMBase list tags do have a corresponding 'container' tag. By use of the container you can easily construct constraints. The size-tag can also be used inside 
  the container to find out how many items the list would have. This is an example of a 'listnodes' with a container.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/query.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/query.jsp" %></td>
</tr>
<tr><td colspan="2">
  The same thing can also be done for the 'clusternodes' version of the list-tags.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/query.cluster.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/query.cluster.jsp" %></td>
</tr>
<tr><td colspan="2">
  Here we see an example for the relatednodes container. It applies also a sortorder.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/query.relatednodes.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/query.relatednodes.jsp" %></td>
</tr>
<tr><td colspan="2">
  The relatednodescontainer can also be given a 'path'. That's useful especially for ordering.
</td></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/query.relatednodes2.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/query.relatednodes2.jsp" %></td>
</tr>
</table>
</mm:cloud>
</body>
</html>
</mm:content>
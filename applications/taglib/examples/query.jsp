<%@taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@ page session="false" import="org.mmbase.bridge.*,org.mmbase.storage.search.*"%>
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
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/query.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/query.jsp" %></td>
</tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/query.cluster.jsp" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/query.cluster.jsp" %></td>
</tr>
</table>
</mm:cloud>
</body>
</html>

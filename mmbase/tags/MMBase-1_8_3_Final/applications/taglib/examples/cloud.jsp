<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@ page session="false" errorPage="error.jsp"  %>
<html>
<head>
  <title>Cloud tag</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
<%@ include file="menu.jsp"%>

<h1>Cloud tag</h1>
<p>
The cloud tag has to appear on most pages, because only inside a cloud
tag, the MMBase data is available. It works like this:
</p>

<%-- <mm:cloud method="anonymous"> --%>
<%-- <mm:cloud method="http"> --%>
<%-- <mm:cloud logon="foo" method="http"> --%>
<%-- <mm:cloud logon="foo" pwd="bar"> --%>
<%-- <mm:cloud id="cloud" method="anonymous"> --%>

<table>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/cloud.jsp.1" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/cloud.jsp.1" %></td>
</tr>
</table>

<p>
  You can also login with the cloud tag. This is demonstrated <a href="<mm:url page="cloud2.jsp" />">here</a> (you'll need name/password)
</p>
</body>
</html>

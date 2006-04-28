<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@ page session="false" errorPage="error.jsp" %>
<html>
<head>
<title>Http authentication</title>
<link href="style.css" rel="stylesheet" type="text/css" media="screen"  />
</head>
<body>
<%@ include file="menu.jsp"%>

<h1>Cloud tag</h1>
<p>
  The 'method' attribute can be used to force http authentication.
</p>
<table>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/cloud.jsp.2" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/cloud.jsp.2" %></td>
</tr>
</table>
</body>
</html>

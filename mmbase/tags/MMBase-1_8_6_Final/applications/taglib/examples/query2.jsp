<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@ page errorPage="error.jsp" session="false"  %><mm:content type="text/html">
<html>
<head>
  <title>Queries with MMBase taglib (1.7)</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
<%@ include file="menu.jsp"%>

<h1>Container tags (more)</h1>

<mm:cloud>
  <p>  
    The query container tags can also be used to make search-tools.
  </p>
  <%@include file="codesamples/containersearch.jsp" %>
  <hr />
  <pre><mm:formatter format="escapexml"><mm:include cite="true" page="codesamples/containersearch.jsp" /></mm:formatter></pre>
</mm:cloud>
</body>
</html>
</mm:content>
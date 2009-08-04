<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page errorPage="error.jsp" session="false"%>
<mm:content type="text/html">
<mm:cloud rank="administrator">

<html>

<head>
  <title>Node manager info</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Nodemanager info</h1>

<p>This example shows how to retrieve information from the MMBase node managers (which describe the objecttypes). </p>

<%@include file="codesamples/nodemanagerinfo.jsp.1" %>


<hr />
<p>
It was implemented like this:
</p>
<pre><mm:formatter format="escapexml"><mm:include page="codesamples/nodemanagerinfo.jsp.1" /></mm:formatter></pre>

</body>

</html>

</mm:cloud>
</mm:content>

<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page errorPage="error.jsp"%>
<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase" method="http">
	<mm:import externid ="nodemanager"/>
	<mm:import externid ="fieldname" />
	<mm:import externid ="create" />

<html>

<head>
  <title>Taglib examples</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Create an object</h1>

<p>
  We demonstrate a simple editor to add new nodes of a specified type.
</p>

<%@include file="codesamples/create.jsp.1" %>

<hr />
<p>
It was implemented like this:
</p>
<pre><mm:formatter format="escapexml"><mm:include page="codesamples/create.jsp.1" /></mm:formatter></pre>


</body>

</html>

</mm:cloud>
</mm:content>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page errorPage="error.jsp"%>
<mm:content type="text/html" expires="0">
<mm:cloud name="mmbase" method="http">
	<mm:import externid ="deletenumber" />
	<mm:import externid ="nodemanager" />
	

<html>

<head>
  <title>Taglib examples</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>List &amp; delete</h1>

<p>
  We demonstrate a simple editor here.
  It allows you to select the nodes from a nodemanager and delete these.
  Note that this example is not optimized, and if a nodemanager contains a lot objects, the system may not be able to handle the request.
  Some objects are protected. You may not be allowed to remove them.
</p>

<%@include file="codesamples/listdelete.jsp.1" %>


<hr />
<p>
It was implemented like this:
</p>
<pre><mm:formatter format="escapexml"><mm:include page="codesamples/listdelete.jsp.1" /></mm:formatter></pre>

</body>

</html>

</mm:cloud>
</mm:content>
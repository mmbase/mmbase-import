<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud name="mmbase">

<html>

<head>
  <title>Taglib examples</title>
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Home</h1>

<p>
The pages found here are made to give some taglib examples. Things are kept
simple so that the page sources are as clear as possible.
</p>

<p>
This page should work after building Tomcat or Orion from the build file. 
</p>

<p>
Here's a simple list of typedefs to see if the MMBase taglib is working:
</p>

<table>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/index.jsp.1" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/index.jsp.1" %></td>
</tr>
</table>
</body>

</html>

</mm:cloud>

<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="text/html">
<mm:cloud>

<html>

<head>
  <title>Taglib examples</title>
  <link href="style.css" rel="stylesheet" type="text/css" />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Home</h1>

<p>
  The pages found here are made to give some taglib examples. Things are kept
  simple so that the page sources are as clear as possible.
</p>
<p>
  A general introduction to taglib can be found <a href="<mm:url page="whatistaglib.jsp" />">here</a>
</p>

<p>
  Some of the examples require the presence of a certain builders (i.e. 'news').
  You can install these builders and there relations by installing the MyNews application.
</p>

<p>
  Here's a list of nodemanagers (typedef objects) which you can use to see if the MMBase taglib is
  working. It is a rather complicated example. On the left, you see
  the source code, and on the right to what it evaluates.
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
</mm:content>
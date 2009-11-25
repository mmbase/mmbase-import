<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><mm:content type="html" expires="0">
<mm:cloud>
<html xmlns="http://www.w3.org/1999/xhtml">


<head>
  <title>Taglib examples</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>

<body>

<%@ include file="menu.jsp"%>

<h1>Home</h1>

<p>
  The pages found here are made to give some taglib examples. Things are kept
  simple so that the page sources are as clear as possible. You may need the 'mynews' example to be installed. 
  You can install this application by going to <a href="<mm:url page="/mmbase/admin/default.jsp?category=admin&amp;subcategory=applications" />">ADMIN ->
  APPLICATIONS</a>, if you have not done so yet (The default name/password is admin/admin2k).
  </p> 
<p>
  A general introduction to taglib can be found <a href="<mm:url page="whatistaglib.jsp" />">here</a>
</p>
<p>
  Here's a list of nodemanagers (typedef objects) which you can use to see if the MMBase taglib is
  working. This is already a rather complicated example. On the left, you see   the source code, and on the right to what it evaluates.
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
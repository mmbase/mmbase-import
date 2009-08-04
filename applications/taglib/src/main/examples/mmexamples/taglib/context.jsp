<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"
%><%@page errorPage="error.jsp" session="false"%><html>
<mm:context scope="request">
<mm:content type="text/html">
<head>
   <title>Contexts</title>
   <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<%@ include file="menu.jsp"%>
<h1>Contexts</h1>
<p>
  A `context' is a scope for taglib variables.
</p>
<p>
To use a context, you can use a context tag. But often you don't have
to, because there is one implicit (unnamed) context. These pages are
using this feature.
</p>
<p>
To start with, a context is empty, you can put things in it e.g. with
the 'import' tag, which gets objects from the outside world (like
parameters), and makes `taglib' variables of them. We now will put
into the variable 'hoi' the value of the parameter 'haj'.
</p>
<table>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/context.jsp.1" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/context.jsp.1" %></td>
</tr>
</table>
<p>
There are several ways to write something from the context to the
page. Here are a few examples with the 'write' tag. The write tag uses
the 'referid' attribute. Such an attribute expects the <em>name</em>
of the variable:
</p>
<table>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/context.jsp.2" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/context.jsp.2" %></td>
</tr>
</table>
<p>
We have also made a jsp variable of it, which can be used as well:
<%= groet.toUpperCase() %>
</p>
<p>
Contextes can be nested. Lets make the contextes A, B and C.
</p>
<table>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/context.jsp.3" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/context.jsp.3" %></td>
</tr>
</table>
<p>
</p>
<table>
<tr><th colspan="2">
If you are not in a context, you can still access the variables of it,
but you have to indicate the full name.

</th></tr>
<tr valign="top">
  <td width="50%"><pre><mm:formatter format="escapexml"><mm:include page="codesamples/context.jsp.4" /></mm:formatter></pre></td>
  <td width="50%"><%@include file="codesamples/context.jsp.4" %></td>
</tr>
<tr><th colspan="2">There is also an attribute 'scope'</th></tr>
<tr valign="top">
  <td width="50%">
    <pre><mm:include cite="true" page="codesamples/context.jsp.5" escape="text/xml" /></pre>
    where context.include.jsp:
    <pre><mm:include cite="true" page="codesamples/context.include.jsp" escape="text/xml" /></pre>

  </td>
  <td width="50%"><%@include file="codesamples/context.jsp.5" %></td>
</tr>
</table>
<p>
Sometimes you also want to use the <em>value</em> of a variable in an attribute. In
that case a construction with a dollar sign ($) must be used. Imagine
for example that one of the variables 'hoi'  must be used in an url:
</p>
<mm:url page="${hoi}.jsp">
 <mm:param name="some_variable">value</mm:param></mm:url>,
<mm:url page="test.${A.hoi}.jsp">
 <mm:param name="some_variable">another_example</mm:param>
</mm:url>,
<mm:url page="test.${A.B.hoi}.jsp">
 <mm:param name="some_variable">third_example</mm:param>
</mm:url>
<br />
<hr />
<a href='<mm:url page="context2.jsp" referids="hoi">
          <mm:param name="hello">saluton</mm:param>
         </mm:url>'>next page</a>
</body>
</html>
</mm:content>
</mm:context>

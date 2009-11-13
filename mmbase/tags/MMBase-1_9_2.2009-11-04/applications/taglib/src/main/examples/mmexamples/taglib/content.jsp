<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<%@ page session="false" errorPage="error.jsp"
%><mm:content type="text/html" language="en" expires="100" escaper="text/html" postprocessor="reducespace">
<html>
<head>
  <title>Contenttag</title>
  <link rel="stylesheet" type="text/css" href="style.css" />
</head>
<body>
<%@ include file="menu.jsp"%>

<h1>Content tag</h1>

<p>
  The content tag is handy on most pages. You can specify the content type with it, the human
  language. It also sets a 'expire' header for http caching front proxies.
</p>
<p>
  It also has an influence on the default 'escaping' behavior of all 'Writer' tags, which makes it easy 
  to print content because it can be escaped well automaticly. There is a default escaper per content type but you can also 
  set one explicitely on the content tag ('escaper' attribute). It can be overridden on every writer if necessary ('escape' attribute).
</p>
<p>
  Finally, the content-tag also provided a way to completely postprocess its body, which is e.g. used a lot to reduce the number of empty 
  lines which would  otherwise appear in your HTML code which is sent to the client.
</p>
<hr />
<a target="_new" href="<mm:url page="showanypage.jsp"><mm:param
name="page"><%=request.getServletPath()%></mm:param></mm:url>">source
of this page</a>.

</body>
</html>
</mm:content>
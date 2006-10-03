<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page errorPage="error.jsp" session="false"%><html>
<mm:content type="text/html">
<html>
<head>
  <title>context 2</title>
  <link href="style.css" rel="stylesheet" type="text/css" media="screen"  />
</head>
<body>
<h1>context 2</h1>
<p>
Two parameters were passed to this page. We show how to use a context
inside another context (inside the default 'context'), and how to use
the `present' and `notpresent' tags.
</p>
<mm:import id="a"     externid="hello"  />
<mm:import id="b"     externid="hi"  />
<mm:import id="alias" externid="hoi"    required ="true" />

<mm:context id="other_context">
  <mm:import externid="alias" />
  <mm:import id="foo">bar</mm:import>
  <mm:write referid="foo" />
</mm:context>
<br />

<mm:present referid="alias">
   param alias is present (<mm:write referid="alias" />)
</mm:present>
<mm:notpresent referid="alias">
   param alias is not present (cannot happen)
</mm:notpresent>
<br />

<mm:present referid="a">
  param hello is present (<mm:write referid="a" />)
</mm:present>
<mm:notpresent referid="a">
  param hello is not present
</mm:notpresent>
<br />

<mm:present referid="b">
  param hi is present (<mm:write referid="b" />)
</mm:present>
<mm:notpresent referid="b">
  param hi is not present
</mm:notpresent>
<hr />
<a href='<mm:url referids="alias" page="context3.jsp" />'>next page</a>
</body>
</html>
</mm:content>

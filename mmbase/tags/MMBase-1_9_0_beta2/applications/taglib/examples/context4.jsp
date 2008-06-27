<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page errorPage="error.jsp" session="false"%>
<mm:content type="text/html">
<html>
<head>
  <title>context 4</title>
  <link href="style.css" rel="stylesheet" type="text/css"/>
</head>
<body>
<h1>context 4</h1>
<mm:import externid="alias" required="true" />
<p>
Reusing the node of the previous page.
</p>
<mm:cloud>
<mm:import externid="first_news_node" required="true" />
<mm:node referid="first_news_node">
     <mm:field name="title" />
</mm:node>
<hr />
<a href='<mm:url page="context5.jsp" referids="alias,first_news_node" />'>next page</a>
</mm:cloud>
</body>
</html>
</mm:content>
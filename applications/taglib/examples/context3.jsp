<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><%@page errorPage="error.jsp" session="false"%><html>
<mm:content type="text/html">
<head>
<title>Contexts</title>
<link href="style.css" rel="stylesheet" type="text/css" media="screen"  />
</head>
<body>
<h1>context 3</h1>
<mm:import externid="alias" required="true" />
<mm:cloud>
<p>
We are going to selecting some news nodes. We use an anonymous context
inside the list, to be able to use ids inside the list without getting
duplicate errors.  For the sake of the example, we explicity use a
node tag inside the listnodes tag, and reuse it immediately to print
another field. Of course, normally is not necessary at all. 
</p>
<table>
<mm:listnodes id="news" type="news" max="10" orderby="number" directions="DOWN" >
<mm:first>
 <tr><th>Title</th><th>Sub title</th></tr>
</mm:first>
<mm:context>
<tr>
<mm:node id="news_node" >
  <td><mm:field name="title" /></td>
</mm:node>
<mm:node referid="news_node">
  <td><mm:field name="subtitle" write="true"><mm:isempty>&nbsp;</mm:isempty></mm:field></td>
</mm:node>
</tr>
</mm:context>
</mm:listnodes>
</table>
<p>
<h3>Reusing the list in the same page</h3>
</p>
<mm:listnodes referid="news">
<mm:first>
The first node we give an id of itself, to export it to the next page.
<mm:node id="first_news_node">
   <mm:field name="title" />
</mm:node>
</mm:first>
</mm:listnodes>
<hr />
<a href='<mm:url page="context4.jsp" referids="alias,first_news_node" />'>next page</a>
</mm:cloud>
</body>
</html>
</mm:content>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context 4</h1>
<p>
Reusing the node of the previous page.
</p>
<mm:cloud>
<mm:import externid="news_node" required="true" />
<mm:node referid="news_node">
     <mm:field name="title" />
</mm:node>
<hr />
<a href='<mm:url page="context5\.jsp" referids="news_node" />'>next page</a>
</mm:cloud>
</body>
</html>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context 3</h1>
<mm:cloud>
<p>
Selecting a news node:
</p>
<mm:listnodes type="news" max="1">
<mm:node id="news_node" >
  <mm:fieldlist type="edit">
    <em><mm:fieldinfo type="guiname" /></em> <mm:fieldinfo type="value" /><br />
  </mm:fieldlist> 
</mm:node>
</mm:listnodes>
<p>
Reusing it in the same page.
</p>
<mm:node referid="news_node">
   <mm:field name="title" />
</mm:node>
<hr />
<a href='<mm:url page="context4\.jsp" referids="news_node" />'>next page</a>
</mm:cloud>
</body>
</html>
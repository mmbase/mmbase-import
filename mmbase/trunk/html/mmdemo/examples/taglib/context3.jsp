<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context 3</h1>
<mm:cloud>
Selecting a news node: <br />
<mm:list type="news" max="1">
<mm:node id="news_node" >
  <mm:fieldlist type="edit">
    <em><mm:fieldinfo type="guiname" /></em> <mm:fieldinfo type="value" /><br />
  </mm:fieldlist> 
</mm:node>
</mm:list>
<br />
Reusing it in the same page:
<mm:node id="news_node">
   <mm:field name="title" />
</mm:node>
<hr />
<a href="context4.jsp">next page</a>
</mm:cloud>
</body>
</html>
<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context 1</h1>
Putting a node in the session context:
<mm:cloud><br />
<mm:context type="session">
<mm:list type="urls" max="1">
<mm:node id="url_node">
  <mm:fieldlist type="edit">
    <em><mm:fieldinfo type="guiname" /></em>:<mm:fieldinfo type="value" /><br />
  </mm:fieldlist> 
</mm:node>
</mm:list>
</mm:context>
<a href="context2.jsp">next page</a>
</mm:cloud>
</body>
</html>
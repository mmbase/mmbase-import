<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" 
%><html>
<body>
<h1>context 2</h1>
<mm:cloud>
<mm:context type="session">
Reusing the node: <br />
<mm:node id="url_node">
  <mm:fieldlist type="edit">
    <em><mm:fieldinfo type="guiname" /></em> <mm:fieldinfo type="value" /><br />
  </mm:fieldlist> 
</mm:node>
Demonstrating export to jsp-variable:<br />
<mm:export type="Node" key="url_node" jspvar="node" />
<% out.println(node.getStringValue("gui()")); %><br />
</mm:context>
</mm:cloud>
<hr />
<a href="context3.jsp">next</a>
</body>
</html>
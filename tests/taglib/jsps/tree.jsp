<%@page import="org.mmbase.bridge.*,org.mmbase.bridge.util.*,java.util.*" 
%><%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm"%>
<mm:content type="text/html">
<html>
<head>
  <title>Testing MMBase/taglib</title>
</head>
<body>
  <mm:import externid="startnodes" vartype="string" required="true" jspvar="startnodes" />
  <h1>Testing MMBase/taglib</h1>
  <mm:cloud jspvar="cloud">
<%
  NodeManager object = cloud.getNodeManager("object");

  NodeQuery q = object.createQuery();
  Queries.addStartNodes(q, startnodes);
  TreeList tree = new TreeList(q, new TreeList.PathElement [] {new TreeList.PathElement(object, null, "destination")}, 5);
  

  TreeList.TreeIterator i = tree.treeIterator();
  while (i.hasNext()) {
    Node n = i.nextNode();
    out.println("" +  i.currentDepth() + " : " + n.getFunctionValue("gui", null).toString() + "<br />");
  }
  out.println("size: " + tree.size() + "<br />");

%>
</mm:cloud>
</body>
</html>
</mm:content>
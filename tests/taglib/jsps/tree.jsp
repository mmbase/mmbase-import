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

<table>
<tr>

<td>
<mm:listnodescontainer type="object">
  <mm:constraint field="number" operator="IN" value="$startnodes" />
  <mm:tree type="object" searchdir="destination" maxdepth="8">
    <mm:grows>
      <ul>
    </mm:grows>
    <li><mm:nodeinfo type="guitype" />: <mm:field name="number" /> <mm:function name="gui" escape="none" /></li>
    <mm:shrinks>
      </ul>
    </mm:shrinks>
    <mm:last>
      size: <mm:size />
    </mm:last>
  </mm:tree>
</mm:listnodescontainer>
</td>

<td>
<mm:node number="$startnodes">
<mm:relatednodescontainer type="object" searchdirs="destination">
  <mm:tree type="object" searchdir="destination" maxdepth="8">
    <mm:grows>
      <ul>
    </mm:grows>
    <li><mm:depth />: <mm:nodeinfo type="guitype" />: <mm:field name="number" /> <mm:function name="gui" escape="none" /></li>
    <mm:shrinks>
      </ul>
    </mm:shrinks>
    <mm:last>
      size: <mm:size />
    </mm:last>
  </mm:tree>
</mm:relatednodescontainer>
</mm:node>
</td>

</tr>


<hr />
<%--
  NodeManager object = cloud.getNodeManager("object");

  NodeQuery q = object.createQuery();
  Queries.addStartNodes(q, startnodes);

  {
    TreeList tree = new TreeList(q);

    tree.grow(object, null, "destination");
    tree.grow(object, null, "destination");

    TreeIterator i = tree.treeIterator();
    while (i.hasNext()) {
      Node n = (Node) i.next();
      out.println("" +  i.currentDepth() + " : " + n.getNumber() + " : " + n.getIntValue("otype") + "/" +  n.getFunctionValue("gui", null).toString() + "<br />");
    }
    out.println("size: " + tree.size() + "<br />"); 
  }


--%>

</mm:cloud>
</body>
</html>
</mm:content>
<%@page session="false" import="org.mmbase.bridge.*,org.mmbase.bridge.util.*,java.util.*" 
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

<mm:import id="test" vartype="list">bla,koe</mm:import>
<mm:stringlist referid="test" > <mm:write />abc </mm:stringlist>
<mm:write value="hoi" write="true"> bla bla </mm:write>
<table>
<tr>
<mm:log>=============================================</mm:log>
<td>
 <mm:timer>
<mm:listnodescontainer type="object">
  <mm:constraint field="number" operator="IN" value="$startnodes" />
  <mm:tree id="tree" type="object" searchdir="destination" maxdepth="8">

    <mm:grow>
      <ul class="<mm:depth />" id="<mm:index list="tree" />">
      <mm:onshrink></ul><!-- <mm:depth />/<mm:index list="tree" /> --></mm:onshrink>
    </mm:grow>

    <li class="<mm:depth />" id="<mm:index />"><mm:nodeinfo type="guitype" />: <mm:field name="number" /> <mm:function name="gui" escape="none" /></li>

    <mm:onshrink></li><!-- <mm:depth />/<mm:index list="tree" /> --></mm:onshrink>

    <mm:shrink />

    <mm:last>
      size: <mm:size />
    </mm:last>

  </mm:tree>
</mm:listnodescontainer>
</mm:timer>
</td>
<mm:log>-------------------------------------------</mm:log>
<td>
 <mm:timer>
<mm:node number="$startnodes">
<mm:relatednodescontainer type="object" searchdirs="destination">
  <mm:tree type="object" searchdir="destination" maxdepth="8">
    <mm:grow>
      <ul><mm:onshrink></ul></mm:onshrink>
    </mm:grow>
    <li><mm:depth />: <mm:nodeinfo type="guitype" />: <mm:field name="number" /> <mm:function name="gui" escape="none" />

    <mm:onshrink></li></mm:onshrink>

    <mm:shrink />

    <mm:last>
      size: <mm:size />
    </mm:last>
  </mm:tree>
</mm:relatednodescontainer>
</mm:node>
</mm:timer>
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
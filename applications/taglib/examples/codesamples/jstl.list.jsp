<mm:import externid="lijstje" vartype="list">5,6,7,8,9</mm:import>

<c:forEach var="index" items="${lijstje}">
  <mm:write value="$index" />
</c:forEach>

<mm:listnodes type="object" max="5" id="nodes" orderby="number" directions="down" />

<c:forEach var="node" items="${nodes}">
  <mm:node referid="node">
    <p><mm:nodeinfo type="guitype" />: <mm:function name="gui" /></p>
  </mm:node>
</c:forEach>

${nodes}
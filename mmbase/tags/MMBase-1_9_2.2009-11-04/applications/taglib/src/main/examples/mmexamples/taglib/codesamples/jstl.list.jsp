<mm:import externid="lijstje" vartype="list">5,6,7,8,9</mm:import>

<c:forEach var="index" items="${lijstje}">
  <mm:write value="$index" /> <mm:last inverse="true">, </mm:last>
</c:forEach>

<mm:listnodes type="object" max="5" id="nodes" orderby="number" directions="down" />

<c:forEach var="node" items="${nodes}">
  <p>
    <mm:index />: 
    <mm:node referid="node">
      <mm:nodeinfo type="guitype" />: <mm:function name="gui" />
    </mm:node>
  </p>
</c:forEach>

${nodes}
<hr />

<mm:listnodes id="typedef" max="5" type="typedef">
  <p>${typedef}: ${typedef.name}</p>
</mm:listnodes>

After a list, a the variable is updated to a list: ${typedef[0].name}


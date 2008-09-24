<mm:import id="operations" vartype="list"><mm:write referid="visibleoperations" /></mm:import>
<mm:functioncontainer>
  <mm:param name="grouporuser"><mm:field name="number" /></mm:param>
  <mm:listnodes id="thiscontext" type="mmbasecontexts">  
    <mm:stringlist referid="operations">
      <mm:param name="operation"><mm:write /></mm:param>
      <mm:import id="right" externid="$_:$thiscontext" />
      <mm:compare referid="right" value="on">
        <mm:voidfunction name="grant" />
      </mm:compare>
      <mm:compare referid="right" value="on" inverse="true">
        <mm:voidfunction  name="revoke" />
      </mm:compare>
    </mm:stringlist>
  </mm:listnodes>
</mm:functioncontainer>
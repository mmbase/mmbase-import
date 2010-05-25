<mm:import id="varstatuslist" vartype="list">a,b,c,d</mm:import>
<mm:stringlist referid="varstatuslist" varStatus="status">
  ${status.index}: ${status.current}
  <mm:last inverse="true">,</mm:last>
</mm:stringlist>

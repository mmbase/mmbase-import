<mm:import id="urls" vartype="list">,.,/</mm:import>
<mm:import id="refs" vartype="list">false,server,context,true</mm:import>
<mm:stringlist id="page" referid="urls">
  <h2>Page '${page}'</h2>
  <mm:stringlist referid="refs">
    <p>${_}: <mm:url id="id$_" page="$page" absolute="$_" /></p>
  </mm:stringlist>
  <h1>Reuse with EL</h1>
  <p>${idfalse}, ${idserver}, ${idcontext}, ${idtrue}</p>
  <mm:stringlist referid="refs">
    <h1>Reuse ${_}</h1>
    <p><mm:url referid="id$_" /></p>
    <p><mm:url referid="id$_" absolute="server" /></p>
    <p><mm:url referid="id$_" absolute="context" /></p>
    <p><mm:url referid="id$_" absolute="true" /></p>
  </mm:stringlist>
</mm:stringlist>
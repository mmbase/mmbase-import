<mm:import id="urls" vartype="list">,.,/</mm:import>
<mm:import id="refs" vartype="list">false,server,context,true</mm:import>
<mm:import id="d">D</mm:import>
<mm:import id="e">E</mm:import>
<mm:stringlist id="page" referid="urls">
  <h2>Page '${page}'</h2>
  <mm:stringlist referid="refs">
    <p>${_}: <mm:url id="id$_" page="$page" absolute="$_" referids="d"><mm:param name="a" value="A" /></mm:url></p>
  </mm:stringlist>
  <h1>Reuse with EL</h1>
  <p>${idfalse}</p>
  <p>${idserver}</p>
  <p>${idcontext}</p>
  <p>${idtrue}</p>
  <mm:stringlist referid="refs">
    <h1>Reuse ${page} abs=${_}</h1>
    <p>abs=false <mm:url referid="id$_" referids="e"><mm:param name="b">B</mm:param></mm:url></p>
    <p>abs=server <mm:url referid="id$_" absolute="server" referids="e"><mm:param name="b">B</mm:param></mm:url></p>
    <p>abs=context <mm:url referid="id$_" absolute="context" referids="e"><mm:param name="b">B</mm:param></mm:url></p>
    <p>abs=true  <mm:url referid="id$_" absolute="true" referids="e"><mm:param name="b">B</mm:param></mm:url></p>
  </mm:stringlist>
</mm:stringlist>
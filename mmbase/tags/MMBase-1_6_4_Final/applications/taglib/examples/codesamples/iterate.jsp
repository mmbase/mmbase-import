<mm:import id="langs" vartype="list">es,de,it,nl,zh,ja,ar,el,he,ru</mm:import>
<!-- of course an alias list is only a list of strings, so we can (ab)use it: -->
<mm:aliaslist referid="langs">
  <mm:locale language="$_"><mm:time time="now" format="MMMM" /></mm:locale>
  <mm:last inverse="true">,</mm:last>
</mm:aliaslist>



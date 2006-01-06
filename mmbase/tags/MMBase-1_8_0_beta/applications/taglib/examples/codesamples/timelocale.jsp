<font color="red">A few cool datetime results:</font><br />
<mm:import id="langs" vartype="list">es,de,it,nl,zh,ja,ar,el,he,ru</mm:import>
<mm:aliaslist referid="langs">
<mm:locale language="$_"><mm:time time="now" format=":FULL.FULL" /></mm:locale><br />
</mm:aliaslist>
<font color="red">Only a date:</font><br />
<mm:locale language="fr"><mm:time time="now" format=":LONG" /></mm:locale><br />
<font color="red">Only a time:</font><br />
<mm:locale language="nl"><mm:time time="now" format=":.MEDIUM" /></mm:locale><br />
<font color="red">SimpleDateFormat:</font><br />
<mm:locale language="pl">:<mm:time time="now" format="MMMM yyyy" /></mm:locale><br />



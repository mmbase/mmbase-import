<mm:import id="days" vartype="list">monday,tuesday,wednesday,thursday,friday,saturday,sunday</mm:import>

<mm:stringlist referid="days">
test1 <mm:index/> <mm:time time="$_" format=":FULL.FULL" />
</mm:stringlist>

change the offset <mm:import id="offset"><%= - 60 * 60 * 24 * 7 %></mm:import>

<mm:stringlist referid="days">
test2 <mm:index/> <mm:time time="$_" offset="$offset" format=":FULL.FULL" />
</mm:stringlist>

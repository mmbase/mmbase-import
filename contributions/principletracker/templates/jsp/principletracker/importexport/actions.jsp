<%@ taglib uri="http://www.mmbase.org/mmbase-taglib-1.0" prefix="mm" %>
<mm:cloud rank="basic user">
<mm:import externid="action" />

<mm:compare value="exportprincipleset" referid="action">
	<mm:import externid="exportsetid" />
	<mm:import externid="filepath" />
	<mm:import externid="wantedstate" />
	<mm:import id="feedback" reset="true"><mm:function set="principletracker" name="exportPrincipleSet" referids="exportsetid,filepath,wantedstate" /></mm:import>           
	<b>*** <mm:write referid="feedback" /> ***</b>
</mm:compare>

<mm:compare value="importprincipleset" referid="action">
	<mm:import externid="setname" />
	<mm:import externid="filepath" />
	<mm:import id="feedback" reset="true"><mm:function set="principletracker" name="importPrincipleSet" referids="setname,filepath" /></mm:import>           
	<b>*** <mm:write referid="feedback" /> ***</b>
</mm:compare>

</mm:cloud>

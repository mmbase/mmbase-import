<mm:import externid="portal" from="parameters">home</mm:import>

<mm:node number="$portal"
	><mm:remove referid="portal"
	/><mm:import id="portal"><mm:field name="number" /></mm:import
></mm:node>

<mm:import externid="page" from="parameters"
	><mm:node number="$portal"
		><mm:relatednodes type="pages" max="1"
			><mm:context
				><mm:aliaslist><mm:write /><mm:import id="aliasfound" /></mm:aliaslist
				><mm:notpresent referid="aliasfound" ><mm:field name="number" /></mm:notpresent
			></mm:context
		></mm:relatednodes
	></mm:node
></mm:import>


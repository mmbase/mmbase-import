<%-- First try if it is present as parameter --%>
<mm:import from="parameters" externid="template" />
<mm:import from="parameters" id="parmurl" externid="url" />

<mm:present referid="parmurl">
  <mm:write id="url" referid="parmurl"  write="false" />
</mm:present>

<mm:notpresent referid="parmurl">
  
  <mm:notpresent referid="template">
	<mm:remove referid="template" />
	<mm:relatednodes type="templates" max="1" role="related">
	  <mm:field id="template" name="number" write="false" />
	</mm:relatednodes>
	<mm:notpresent referid="template">
	  <mm:import id="template">default.template</mm:import>
	</mm:notpresent>
  </mm:notpresent>
  
  <mm:node number="$template">
<!-- using template <mm:write referid="template" /> :  <mm:field  id="url" name="url"  /> -->
  </mm:node>
  
  
  
</mm:notpresent>
<mm:import externid="portal" from="parameters">home</mm:import>

<%-- find 'page' object from parameters, but default to related page of portal (even to alias, if there is one) --%>
<mm:import externid="page" from="parameters"
 ><mm:node number="$portal"
   ><mm:relatednodes type="pages" max="1"
       ><mm:aliaslist><mm:write id="aliasfound" /></mm:aliaslist><mm:notpresent referid="aliasfound" ><mm:field name="number" /></mm:notpresent
   ></mm:relatednodes
 ></mm:node
></mm:import>

<%-- find node number (rather than alias). Why? Needed, because it is compared with a number somewhere --%>
<mm:node number="$portal">
   <mm:remove referid="portal" />
   <mm:field id="portal" name="number" write="false" />
</mm:node>



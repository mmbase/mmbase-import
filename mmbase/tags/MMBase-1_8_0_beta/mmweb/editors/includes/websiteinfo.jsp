<td><a target="_blank" href="<%= websiteRoot %>/index.jsp?website=<mm:aliaslist
				><mm:write /><mm:import id="alias" /></mm:aliaslist
			><mm:notpresent referid="alias"
				><mm:field name="number" 
			/></mm:notpresent
			><mm:remove referid="alias"
		/>"><mm:field name="title" />
	</a></td>
<td><a href="<mm:url referids="referrer" page="/mmapps/editwizard/jsp/wizard.jsp"> 
					<mm:param name="wizard">wizards/site/site</mm:param> 
					<mm:param name="nodepath">site</mm:param>
					<mm:param name="objectnumber"><mm:field name="number" /></mm:param>
			</mm:url>"><mm:field name="title" /></a>
</td>
<td><mm:aliaslist>
		<mm:write jspvar="oalias_name" vartype="String" write="false">
		<mm:list nodes="" path="oalias" constraints="<%= "oalias.name='"+ oalias_name + "'" %>">
		<mm:field name="oalias.number" id="alias_number" write="false" />			
		<a href="<mm:url referids="referrer" page="/mmapps/editwizard/jsp/wizard.jsp"> 
				<mm:param name="wizard">wizards/oalias/oalias</mm:param> 
				<mm:param name="nodepath">oalias</mm:param>
				<mm:param name="objectnumber"><mm:write referid="alias_number" /></mm:param>
			</mm:url>"><%= oalias_name %></a> <mm:last inverse="true">,</mm:last>
		<mm:remove referid="alias_number" />
		</mm:list>
		</mm:write>
		<mm:import id="alias" />
	</mm:aliaslist>
	<mm:notpresent referid="alias"
		><a href="<mm:url referids="referrer" page="/mmapps/editwizard/jsp/wizard.jsp"> 
				<mm:param name="wizard">wizards/oalias/oalias</mm:param> 
				<mm:param name="nodepath">oalias</mm:param>
				<mm:param name="objectnumber">new</mm:param>
				<mm:param name="origin"><mm:field name="number" /></mm:param>
			</mm:url>"><img onclick="return doDelete('Are you sure you want to add an alias to this website?');" 
				onmousedown="cancelClick=true;"
				title="Add an alias to this website" height="15" width="15" border="0" src="/mmapps/editwizard/media/new.gif"></a>
	</mm:notpresent
	><mm:remove referid="alias"
	/>
 </td>

<% if(pageCounter!=null){ %>
	<mm:listnodes type="mmevents" max="1" orderby="number" directions="DOWN">
		<mm:field name="number" jspvar="mmevents_number" vartype="String" write="false">
				<mm:deletenode number="<%= mmevents_number %>" deleterelations="true" />
		</mm:field>
	</mm:listnodes>
<% } %>
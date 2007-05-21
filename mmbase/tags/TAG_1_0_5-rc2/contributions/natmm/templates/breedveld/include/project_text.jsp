<% String project_text = ""; 
	sFieldName = "omschrijving" + lang; %>
	<mm:field name="<%= sFieldName %>" jspvar="dummy07" vartype="String" write="false">
	<% if (dummy07==null||dummy07.equals("")){ %>
		<mm:field name="omschrijving" jspvar="text" vartype="String" write="false">
		<% project_text = text; %>	
		</mm:field>
	<%	} else {
			project_text = dummy07;
		}
 %></mm:field>
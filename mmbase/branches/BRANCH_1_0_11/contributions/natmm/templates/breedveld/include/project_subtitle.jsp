<% String project_subtitle = ""; 
	sFieldName = "subtitle" + lang;%>
	<mm:field name="<%= sFieldName %>" jspvar="dummy06" vartype="String" write="false">
	<% if (dummy06==null||dummy06.equals("")){ %>
		<mm:field name="subtitle" jspvar="subtitel" vartype="String" write="false">
		<% project_subtitle = subtitel; %>	
		</mm:field>
	<%	} else {
			project_subtitle = dummy06;
		}
 %></mm:field>
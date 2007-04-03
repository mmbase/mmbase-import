<% String project_title = ""; 
	String sFieldName = "titel" + lang; %>
	<mm:field name="<%= sFieldName %>" jspvar="dummy01" vartype="String" write="false">
	<% if (dummy01==null||dummy01.equals("")){ %>
		<mm:field name="titel" jspvar="titel" vartype="String" write="false">
		<% project_title = titel; %>	
		</mm:field>
	<%	} else {
		project_title = dummy01;
		}
 %></mm:field>
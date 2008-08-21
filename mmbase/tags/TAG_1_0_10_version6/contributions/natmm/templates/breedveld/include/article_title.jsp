<% String article_titel = "";
	String sFieldName = "titel" + lang; %>
	<mm:field name="<%= sFieldName %>" jspvar="dummy" vartype="String" write="false">
	<% article_titel = dummy;
		int i = 0;
		while ((article_titel==null||article_titel.equals(""))&&(i<2)) {
			sFieldName = alt_langs[i]; i++;%>
			<mm:field name="<%= sFieldName %>" jspvar="dummy1" vartype="String" write="false">
			<% article_titel = dummy1; %>
			</mm:field>
	<% } %>		
	</mm:field>
<%= article_titel %>
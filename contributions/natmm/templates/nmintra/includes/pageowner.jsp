<%
if(!owners.equals("")) {
	String firstOwner = owners;
	if(firstOwner.indexOf(",")>-1) {
		firstOwner = firstOwner.substring(0,firstOwner.indexOf(","));
	}
	%>
	<mm:node number="<%= firstOwner %>">
		<p>
			<div align="right">
				Informatie over deze pagina:
				<a href="mailto:<mm:field name="emailadres" />?subject=<% 
					%><mm:node number="<%= rootId %>"><mm:field name="title" /></mm:node
					> - <mm:node number="<%= paginaID %>"><mm:field name="title" /></mm:node
					>">
					<mm:field name="titel" />
				</a><% 
				if(postingStr.equals("|action=print")) { 
					%> <mm:field name="emailadres" /><% 
				} %>
			</div>
	  </p>
	 </mm:node>
	 <% 
}
%>
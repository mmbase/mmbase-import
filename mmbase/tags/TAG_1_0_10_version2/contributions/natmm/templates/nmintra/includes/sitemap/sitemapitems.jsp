<% // note: this implementation will only show pages on two levels %>
<mm:node number="<%= subsiteID %>">
	<mm:related path="parent,rubriek" searchdir="destination" orderby="parent.pos" directions="UP">
	<mm:field name="parent.pos" jspvar="parent_pos" vartype="String" write="false">
	<% 
	if((Integer.parseInt(parent_pos) % numberOfColumns) == (colNumber-1)){ 
		String itemsClassName = "" ;
		%>
		<mm:field name="rubriek.number" jspvar="rubriek_number" vartype="String" write="false">
			<mm:node number="<%= rubriek_number %>">
				<mm:related path="related,style" fields="style.title">
					<mm:field name="style.title" jspvar="style_title" vartype="String" write="false">
						<% itemsClassName = style_title; %>
					</mm:field>
				</mm:related>
				<table cellpadding="0" cellspacing="0" border="0">
				<tr>
					<td>
						<span class="pageheader"><span class="dark_<%= itemsClassName %>"><mm:field name="naam" /></span></span>
					</td>
				</tr>
				<tr><td><img src="media/spacer.gif" width="10" height="1"></td></tr>
				<mm:related path="posrel,pagina" orderby="posrel.pos" directions="UP">
					<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
					<mm:first>
					</mm:first>
					<tr><td>
						<a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>">
							<span class="normal"><mm:field name="pagina.titel" /></span>
						</a>
					</td></tr>
					</mm:field>
				</mm:related>
				<%
				
				// lets look whether there are subpages under this page
				%>
				<mm:related path="parent,rubriek,posrel,pagina"
					orderby="parent.pos,posrel.pos" directions="UP,UP"
					fields="pagina.titel,pagina.number">
					<mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
					<tr>
						<td>
						&nbsp;&nbsp;&nbsp;
						<a href="<%= ph.createPaginaUrl(pagina_number,request.getContextPath()) %>">
							<span class="normal"><mm:field name="pagina.titel" /></span>
						</a>
						</td>
					</tr>
					</mm:field>
				</mm:related>
				</table>
				<img src="media/spacer.gif" width="225" height="10">
			</mm:node>
		</mm:field><%
	} 
	%>
	</mm:field>
	</mm:related>
</mm:node>
<img src="media/logo_175.gif" align="right">
<% if(rubriekId.equals("-1")) { 
	%><mm:node number="<%= paginaID %>"
	><p><mm:field name="titel" /><br><span class="projecttitle"><mm:field name="kortetitel" 
		/></span></p></mm:node
	><% 
} else { 
	%><mm:node number="<%= rubriekId %>" 
		><mm:present referid="ishomepage"
			><p><mm:field name="naam_eng"/><br><span class="projecttitle"><mm:field name="naam"/></span></p></mm:present
		><mm:notpresent referid="ishomepage"
			><p><mm:field name="naam" /><br><span class="projecttitle"><mm:node number="<%= paginaID %>"
				><mm:field name="titel" /></mm:node></span></p></mm:notpresent
		><mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" orderby="posrel.pos" directions="UP" max="1"
			><mm:field name="posrel.pos" jspvar="iPos" vartype="Integer" write="false">
				<% if (iPos.intValue()<-1) { %>
					<mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" constraints="posrel.pos=-1">
						<mm:field name="pagina.number" jspvar="thispage_number" vartype="String" write="false">
							<%
							if(!thispage_number.equals(paginaID)) {
								%><a href="<%= ph.createPaginaUrl(thispage_number,request.getContextPath()) %>"><% 
							} 
							%><mm:field name="pagina.titel" 
							/><% if(!thispage_number.equals(paginaID)) { %></a><% }
						%></mm:field>
					</mm:list>
					<mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" constraints="posrel.pos!=-1" orderby="posrel.pos" directions="UP">
						<mm:field name="pagina.number" jspvar="thispage_number" vartype="String" write="false">
							<%= " | " %><% 
							if(!thispage_number.equals(paginaID)) { 
								%><a href="<%= ph.createPaginaUrl(thispage_number,request.getContextPath()) %>"><% 
							} 
							%><mm:field name="pagina.titel" 
							/><% if(!thispage_number.equals(paginaID)) { %></a><% }
					%></mm:field>
					</mm:list>
				<% } else { %>	
					<mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" orderby="posrel.pos" directions="UP">
						<mm:field name="pagina.number" jspvar="thispage_number" vartype="String" write="false"
							><mm:first inverse="true"> | </mm:first><%
							if(!thispage_number.equals(paginaID)) { 
								%><a href="<%= ph.createPaginaUrl(thispage_number,request.getContextPath()) %>"><%
							} 
							%><mm:field name="pagina.titel" 
							/><% if(!thispage_number.equals(paginaID)) { %></a><% }
						%></mm:field>
					</mm:list>	
				<% }%>
			</mm:field>
		</mm:list
	></mm:node><%
} %>
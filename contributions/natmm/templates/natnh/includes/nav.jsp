<img src="media/logo_175.gif" align="right">
<mm:node number="<%= rubriekId %>" 
	><mm:present referid="ishomepage"
		><p><mm:field name="naam_eng"/><br><span class="projecttitle"><mm:field name="naam"/></span></p></mm:present
	><mm:notpresent referid="ishomepage"
		><p><mm:field name="naam" /><br><span class="projecttitle"><mm:node number="<%= paginaID %>"
			><mm:field name="titel" /></mm:node></span></p></mm:notpresent
	><mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" orderby="posrel.pos" directions="UP" max="1"
		><mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" orderby="posrel.pos" directions="UP"
			><mm:size
				><mm:compare value="1" inverse="true"
					><mm:field name="pagina.number" jspvar="thispage_number" vartype="String" write="false"
						><mm:first inverse="true"> | </mm:first><%
						if(!thispage_number.equals(paginaID)) { 
							%><a href="<%= ph.createPaginaUrl(thispage_number,request.getContextPath()) %>"><%
						} 
							%><mm:field name="pagina.titel" /><% 
						if(!thispage_number.equals(paginaID)) { 
							%></a><%
						}
					%></mm:field
				></mm:compare
			></mm:size
		></mm:list
	></mm:list
></mm:node>
<% if(true) {	
	String breadCrumPath = "";
	boolean foundWebsiteOrPortal = false;
	%><mm:import id="thispage"><%= paginaID %></mm:import
	><mm:node number="<%= articleId %>" notfound="skipbody"
		><mm:field name="titel_zichtbaar"
			><mm:compare value="0" inverse="true"
			  ><% showEndLeaf= false; %>
			</mm:compare
		></mm:field
		><%	if(showEndLeaf) {
			%><mm:field name="titel" jspvar="artikel_titel" vartype="String" write="false"
				><%	breadCrumPath = "<span " +  breadCrumClass + ">" + artikel_titel  + "</span>"; 
			%></mm:field
		><% } 
		%><mm:import id="endleaffound"
	/></mm:node
	><mm:node number="$thispage" notfound="skipbody"
		><% String titel = "";
			String firstRubriekPageId = paginaID;
		%><mm:list nodes="$thispage" path="pagina,posrel,rubriek" max="1"
			><% foundWebsiteOrPortal = true;
			%><mm:field name="rubriek.naam" jspvar="rubriek_name" vartype="String" write="false"
					><% titel = rubriek_name;
			%></mm:field
			><mm:field name="rubriek.number" jspvar="rubriek_number" vartype="String" write="false">
				<mm:list nodes="<%= rubriek_number %>" path="rubriek,posrel,pagina" orderby="posrel.pos" directions="UP" max="1">
					<mm:field name="posrel.pos" jspvar="iPos" vartype="Integer" write="false">
						<% if (iPos.intValue()<-1) { %>
							<mm:list nodes="<%= rubriek_number %>" path="rubriek,posrel,pagina" constraints="posrel.pos=-1">
								<mm:field name="pagina.number" jspvar="dummy" vartype="String" write="false">
									<% firstRubriekPageId = dummy; %>
								</mm:field>
							</mm:list>
						<% } else {%>
							<mm:field name="pagina.number" jspvar="dummy" vartype="String" write="false">
								<% firstRubriekPageId = dummy; %>
							</mm:field>
						<% } %>
					</mm:field>
					<mm:node element="rubriek">
						<mm:aliaslist>
							<mm:write jspvar="rubriek_alias" vartype="String" write="false">
							<% if (rubriek_alias.equals(rootId)) {%>	
								<mm:import id="first_page"/>
							<% } %>
							</mm:write>
						</mm:aliaslist>
					</mm:node>
				</mm:list>
			 </mm:field>
		</mm:list
		><% if(!foundWebsiteOrPortal) { 
			%><mm:field name="titel" jspvar="pagina_titel" vartype="String" write="false"
				><%	titel = pagina_titel;
			%></mm:field><%	
		}%>
		<mm:notpresent referid="endleaffound"
			><mm:field name="titel" jspvar="pagina_titel" vartype="String" write="false"
				><%	if(showEndLeaf) breadCrumPath = "<span " +  breadCrumClass + ">" + pagina_titel  + "</span>"; 
			%></mm:field>
			<mm:import id="endleaffound"
		/></mm:notpresent
		><mm:notpresent referid="first_page"
			><% breadCrumPath = "<a href=\"" + ph.createPaginaUrl(firstRubriekPageId,request.getContextPath())+ "\"" +  breadCrumClass + ">" 
					+ titel + "</a>&nbsp;<span " +  breadCrumClass + ">></span>&nbsp;" + breadCrumPath; 
		%></mm:notpresent
		><mm:present referid="first_page"
			><mm:field name="titel" jspvar="pagina_titel" vartype="String" write="false"
				><%	if(showEndLeaf) breadCrumPath = "<span " +  breadCrumClass + ">" + pagina_titel  + "</span>"; 
			%></mm:field
			><mm:remove referid="first_page"
		/></mm:present
	></mm:node
	><mm:notpresent referid="endleaffound"
				><% if(showEndLeaf) breadCrumPath = "<span " +  breadCrumClass + ">Projecten</span>"; 
			%></mm:notpresent
			><mm:present referid="endleaffound"
				><%	breadCrumPath = "<a href=\"websites.jsp\" " +  breadCrumClass + 
							">Projecten</a>&nbsp;<span " +  breadCrumClass + ">></span>&nbsp;" + breadCrumPath; 
			%></mm:present
		><% if(!showEndLeaf) { breadCrumPath = breadCrumPath.substring(0,breadCrumPath.lastIndexOf("<span")); }
	%><span class="linktext"><%= breadCrumPath %></span>
	<mm:remove referid="thispage"
	/><mm:remove referid="endleaffound"
	/><% 
} %>
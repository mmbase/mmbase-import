	 <mm:list nodes="<%= rubriek_number %>" path="rubriek,posrel,pagina"
                orderby="posrel.pos" directions="UP" max="1"
        ><mm:field name="pagina.number" jspvar="page_number" vartype="String" write="false"><%
        if(isPreview) {             
            if(pageId.equals("")) { pageId = page_number; }
            String thisRubriekClass = "menuItem"; 
            if(rubriekId.equals(rubriek_number)) { thisRubriekClass = "menuItem"; } 
            if(!bIsFirst) { 
                  %><tr><td><img src="media/spacer.gif" width="1" height="7"></td></tr><% 
                  bIsFirst = false;
            } %>
              <tr>
                <td style="padding-left:19px;letter-spacing:1px;"><a href=<%= ph.createPaginaUrl(page_number, request.getRequestURI()) %> class="menuItem<mm:field name="rubriek.number"
                                ><mm:compare value="<%= rubriekId %>"
                                    ><mm:field name="pagina.number"
                                        ><mm:compare value="<%= pageId %>"
                                            ><mm:field name="posrel.pos"
                                                ><mm:compare value="0"
                                                    >Active</mm:compare
                                            ></mm:field
                                        ></mm:compare
                                    ></mm:field
                                ></mm:compare
                            ></mm:field
                            >"><mm:field name="rubriek.naam" /></a>
                </td></tr><%
             }
        %></mm:field
    ></mm:list><%
    if(rubriekId.equals(rubriek_number)) { 

    // *** list the pages ***
    %><mm:list nodes="<%= rubriek_number %>" path="rubriek,posrel,pagina"
            orderby="posrel.pos" directions="UP" constraints="posrel.pos <> '0'"
        ><mm:field name="pagina.number" jspvar="super_page" vartype="String" write="false"><%
        if(isPreview) { 
           if(pageId.equals("")) { pageId = super_page; }
            %><tr>
                <td style="padding-left:19px;"><table border="0" cellpadding="0" cellspacing="0"><tr><td style="color:white;"><li></td>
                    <td style="letter-spacing:1px;"><a href=<%= ph.createPaginaUrl(super_page, request.getRequestURI()) %> class="menuItem<mm:field name="pagina.number"><mm:compare value="<%= pageId %>">Active</mm:compare></mm:field
                            >"><mm:field name="pagina.titel" /></a></td></tr></table>
                </td>
            </tr>
			<% } %>
			</mm:field>
		</mm:list>
<% } %>	
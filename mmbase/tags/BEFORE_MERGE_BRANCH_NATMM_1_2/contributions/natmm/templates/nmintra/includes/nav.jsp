<% if(iRubriekLayout==NMIntraConfig.SUBSITE1_LAYOUT) { %>
<style>
a.menuitem:hover {
    text-decoration: none;
    color: #40FF40
}
a.menuitemactive:link {
    text-decoration: none;
    color: #40FF40
}
a.menuitemactive:visited {
    text-decoration: none;
    color: #40FF40
}
a.menuitemactive:active {
    text-decoration: none;
    color: #40FF40
}
a.menuitemactive:hover {
    text-decoration: none;
    color: #40FF40
}
</style>
<% } %>
<div class="navlist" id="navlist">
  <table border="0" cellpadding="0" cellspacing="0">
      <tr>
          <td><img src="media/spacer.gif" width="158" height="35"></td>
      </tr>
      <mm:log jspvar="log">
      <%
      // *** referer is used to open navigation on a page which is not visible in the navigation ***
      String tmp_paginaID = paginaID;
      if(!refererId.equals("")) { 
         boolean pageIsVisible = false;
         %><mm:list nodes="<%= rubriekId %>" path="rubriek,posrel,pagina" max="1" constraints="<%= "pagina.number='" + paginaID + "'" %>"><%
            pageIsVisible = true;
         %></mm:list><%
         if(!pageIsVisible) { 
            %><mm:list nodes="<%= rubriekId %>" path="rubriek1,parent,rubriek2,posrel,pagina" max="1" constraints="<%= "pagina.number='" + paginaID + "'" %>"><%
               pageIsVisible = true;
            %></mm:list><%
         }
         if(!pageIsVisible) { paginaID = refererId; }
      }
      
      // show all subObjects for the subsiteID, both pages and rubrieken
      RubriekHelper rubriekHelper = new RubriekHelper(cloud);

      TreeMap [] nodesAtLevel = new TreeMap[10];
      nodesAtLevel[0] = (TreeMap) rubriekHelper.getSubObjects(subsiteID);
      boolean showFirstSubpage = nl.leocms.applications.NMIntraConfig.showFirstSubpage;
      boolean [] isFirstSubpage =  new boolean[10];
      for(int i = 0; i<10; i++) { isFirstSubpage[i] = true; }
      
      int depth = 0;
      
      // invariant: depth = level of present leafs (root has level 0)
      while(depth>-1&&depth<10) { 
         
         if(nodesAtLevel[depth].isEmpty()) {
           
			     // if this nodesAtLevel is empty, try one level back
           depth--; 
         }
         if(depth>-1&&!nodesAtLevel[depth].isEmpty()) {

			   // show all subObjects, both pages and rubrieken
				 while(! nodesAtLevel[depth].isEmpty()) { 

					Integer thisKey = (Integer) nodesAtLevel[depth].firstKey();
					String sThisObject = (String) nodesAtLevel[depth].get(thisKey);
					nodesAtLevel[depth].remove(thisKey);
					%><mm:node number="<%= sThisObject %>" jspvar="thisObject"
						><mm:nodeinfo  type="type" write="false" jspvar="nType" vartype="String"><%
							
              boolean showPage = showFirstSubpage||!isFirstSubpage[depth];
              isFirstSubpage[depth] = false;
							
              if(nType.equals("pagina")) { // show page
                
								if(showPage) { // don't show first subpage of rubriek

                  // the page can be a redirect to another page
									String sThisPage = sThisObject;
									%><mm:related path="rolerel,pagina" searchdir="destination"
										><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"><%
											sThisPage = pagina_number; 
										%></mm:field
									></mm:related>
									<mm:node number="<%= sThisPage %>" jspvar="thisPage">
										<tr>
											<td>
												<table border="0" cellpadding="0" cellspacing="0">
													<tr>
														<%@include file="rubriek_page.jsp" %>
														<td style="letter-spacing:1px;">
														<a href="<%= ph.createPaginaUrl(sThisPage,request.getContextPath()) 
															%>" class="menuItem<mm:field name="number"><mm:compare value="<%= paginaID %>">Active</mm:compare></mm:field
															>"><mm:field name="titel"/></a>
														</td>
													</tr>
												</table>
											</td>
										</tr>
									</mm:node>
									<%
								}
								
							} else { // show rubriek, which is a link to the first page in the rubriek
								
								String nextPage =  rubriekHelper.getFirstPage(sThisObject);
								%>
								<tr>
									<td>
										<table border="0" cellpadding="0" cellspacing="0">
											<tr>
												<%@include file="rubriek_page.jsp" %>
												<td style="letter-spacing:1px;">
													<a href="<%= ph.createPaginaUrl(nextPage,request.getContextPath()) %>"
														class="menuItem<%
															if(sThisObject.equals(rubriekId) && nextPage.equals(paginaID) ) {
																%>Active<%
															} %>"><mm:field name="naam" /></a>
												</td>
											</tr>
                                 
                                       <mm:field name="naam" jspvar="navTitel" vartype="String" write="false">
                                          <% 
                                          if (navTitel.toLowerCase().equals("home")) { %>
                                             <tr><td></td><td>&nbsp;</td></tr>
                                             <tr><td></td><td>&nbsp;</td></tr>
                                          <% } %>
                                       </mm:field>                                 
                                 
										</table>
									</td>
								</tr>
								<%
								if(breadcrumbs.contains(sThisObject)) {
									// this rubriek is in the breadcrumbs, so show its subobjects in the next iteration
								  depth++;
									nodesAtLevel[depth] = (TreeMap) rubriekHelper.getSubObjects(sThisObject);
								}
                
							} 
						%></mm:nodeinfo
					></mm:node><%
				} 
         } 
      } 

      // reset paginaID to original value, if referer is used
      if(!refererId.equals("")) { paginaID = tmp_paginaID; }
      %>
      </mm:log>
   </table>
</div>

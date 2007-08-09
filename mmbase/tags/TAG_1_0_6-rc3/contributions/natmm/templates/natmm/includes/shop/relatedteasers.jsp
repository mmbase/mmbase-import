<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<% PaginaHelper ph = new PaginaHelper(cloud); %>
<mm:list nodes="<%= paginaID %>" path="pagina,rolerel,teaser" constraints="rolerel.rol='2'"	orderby="rolerel.pos" directions="UP"
	><mm:node element="teaser"
	><mm:field name="titel_zichtbaar" write="false" jspvar="teaser_tz" vartype="String"
   ><mm:field name="titel_eng" jspvar="type" vartype="String" write="false"><%
  
  if(!type.equals("link_set")) { 
	
		String shop_itemHref = "";
		%><mm:related path="readmore,pagina" fields="pagina.number"
			><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"><% 
				shop_itemHref =  ph.createPaginaUrl(pagina_number,request.getContextPath()); 
				%><mm:remove referid="readmoretext" 
				/><mm:import id="readmoretext"><mm:field name="readmore.readmore" /></mm:import
			></mm:field
		></mm:related><% 
		if(shop_itemHref.equals("")) {
			%><mm:related path="readmore,items,posrel,pagina" fields="items.number,pagina.number"
				><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"
				><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"><%
				   shop_itemHref =  ph.createPaginaUrl(pagina_number,request.getContextPath()) + "&u=" + items_number; 
					%><mm:remove referid="readmoretext" 
					/><mm:import id="readmoretext"><mm:field name="readmore.readmore" /></mm:import
				></mm:field
				></mm:field
			></mm:related><%
	} 
		
	if(type.equals("standaard")) { // **************** standard teaser ************************
		
			%><table width="100%" cellspacing="0" cellpadding="0">
				<tr>
					<td style="padding:4px;padding-bottom:2px;padding-top:7px;" class="colortitle">
               <% if(!teaser_tz.equals("0")){ %><mm:field name="titel" /><% } %>
					<table width="100%" cellspacing="0" cellpadding="0">
						<tr>
							<td><mm:field name="omschrijving"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field></td>
						</tr>
					</table>
					</td>
				</tr><%	

				if(!shop_itemHref.equals("")) {
					%><tr>
						<td width="100%">
						<table cellspacing="0" cellpadding="0" width="100%"><tr>
							<td style="padding-left:5px;text-align:right;"><a href="<mm:url page="<%= shop_itemHref %>" />" class="maincolor_link_shorty"><span style="font-weight:normal;"><mm:write referid="readmoretext" /></span></a></td>
							<td style="padding-top:2px;padding-left:5px;vertical-align:bottom;"><a href="<mm:url page="<%= shop_itemHref %>" 
								/>"><img src="media/shop/pijl_oranje_op_lichtoranje.gif" border="0" alt=""></a></td>
						</tr></table>
						</td>
					</tr><%
				} 
				%><tr><td><img src="media/trans.gif" width="1" height="7" border="0" alt=""></td></tr>
			</table><%
		
	} else { // *************************************** teaser with flag ************************
		
			%><table width="100%" cellspacing="0" cellpadding="0"  background="media/shop/discountborder.gif" style="background-repeat:repeat-x;">
			<tr>
				<td class="maincolor" style="vertical-align:top;"><img src="media/shop/discountborder.gif" width="1" height="15" border="0" alt=""></td>
				<td style="padding:4px;">
				<img src="media/trans.gif" width="1" height="15" border="0" alt=""><br>
				<% if(!type.equals("border")) { %><img style="float:right;margin-top:-11px;" src="media/shop/<%= type %>.gif"><% }
				if(!shop_itemHref.equals("")) {
					%><a href="<mm:url page="<%= shop_itemHref %>" />" class="maincolor_link_shorty">
                  <% if(!teaser_tz.equals("0")){ %><mm:field name="titel" /><% } %>
                </a><%
				} else {
					%><span class="colortitle"><% if(!teaser_tz.equals("0")){ %><mm:field name="titel" /><% } %></span><% } 
				%><table width="100%" cellspacing="0" cellpadding="0">
					<tr>
						<td><mm:field name="omschrijving"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field></td>
					</tr>
				</table>
				</td>
				<td class="maincolor" style="vertical-align:top;"><img src="media/shop/discountborder.gif" width="1" height="15" border="0" alt=""></td>
			</tr><%	

			if(!shop_itemHref.equals("")) {
				%><tr>
					<td class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
					<td class="footer" width="100%">
					<table cellspacing="0" cellpadding="0" width="100%"><tr>
						<td class="nav" style="padding-left:4px;text-align:right;width:100%">
							<a href="<mm:url page="<%= shop_itemHref %>" />" class="nav"><mm:write referid="readmoretext" /></a></td>
						<td style="padding:2px;padding-left:5px;vertical-align:bottom;"><a href="<mm:url page="<%= shop_itemHref %>" 
							/>"><img src="media/shop/pijl_oranje_op_lichtoranje.gif" border="0" alt=""></a></td>
					</tr></table>
					</td>
					<td class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
				</tr><%
			} 
			%><tr><td class="maincolor" colspan="3"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td></tr>
			</table><%
		}
	}
%></mm:field
></mm:field
></mm:node
></mm:list>
</mm:cloud>
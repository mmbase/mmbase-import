<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<%@include file="../shoppingcart/vars.jsp" %>
<mm:cloud jspvar="cloud">
<% 
PaginaHelper ph = new PaginaHelper(cloud);
%>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" orderby="contentrel.pos"
   ><mm:node element="artikel"
	><table width="100%" cellspacing="0" cellpadding="0" background="media/shop/discountborder.gif" style="background-repeat:repeat-x;">
	<tr>
		<td class="maincolor" style="vertical-align:top;"><img src="media/shop/discountborder.gif" width="1" height="15" border="0" alt=""></td>
		<td class="nav" background="<mm:related path="posrel,images" max="1"
					><mm:node element="images"
						><mm:image template="s(140x180)" 
					/></mm:node
				></mm:related>" style="padding:4px;">
		<img src="media/trans.gif" width="1" height="15" border="0" alt=""><br>
		<mm:field name="titel_eng"
			><mm:isnotempty
				><mm:compare value="standaard" inverse="true"
					><img style="float:right;margin-top:-11px;margin-right:5px;" src="media/shop/<mm:write />.gif"></mm:compare
			></mm:isnotempty
		></mm:field
		><mm:field name="titel" 
		/><table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="60%">
				<mm:field name="intro"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
				<mm:field name="tekst"><mm:isnotempty><mm:write /></mm:isnotempty></mm:field>
        <%
        // this can show multiple items, the price of the last item is shown
        %>
				<mm:related path="readmore,items,posrel,pagina" fields="items.number,pagina.number" orderby="readmore.readmore" directions="UP"
					><mm:field name="pagina.number" jspvar="pages_number" vartype="String" write="false"
					><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"><%
						shop_itemHref =  ph.createPaginaUrl(pages_number,request.getContextPath()) + "?u=" + items_number; 
					%></mm:field
					></mm:field
					><a href="<mm:url page="<%= shop_itemHref %>" />" class="bold"><mm:field name="items.titel" /></a><br>
					<mm:last
						><mm:node element="items"
							><mm:import id="smallprice" 
							/><%@include file="relatedprice.jsp" 
							%><mm:remove referid="smallprice" 
						/></mm:node
					   ><mm:field name="readmore.readmore" id="readmore" write="false"
               /></mm:last>
				</mm:related> 
				</td>
				<td style="width:40%;text-align:right;vertical-align:bottom;""><a style="display:block;width:auto;height:70;" href="<mm:url page="<%= shop_itemHref %>" 
					/>" ><img src="media/trans.gif" border="0" alt=""></a></td>		
			</tr>
		</table>
		</td>
		<td class="maincolor" style="vertical-align:top;"><img src="media/shop/discountborder.gif" width="1" height="15" border="0" alt=""></td>
	</tr>
	<tr>
		<td class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
		<td class="footer" width="100%">
		<table cellspacing="0" cellpadding="0" align="right"><tr>
			<td class="nav"><a href="<mm:url page="<%= shop_itemHref %>" />" class="nav"><mm:write referid="readmore" /></a></td>
			<td style="padding:2px;padding-left:5px;"><a href="<mm:url page="<%= shop_itemHref %>" />"><img src="media/shop/pijl_oranje_op_lichtoranje.gif" border="0" alt=""></a></td>
		</tr></table>
		</td>
		<td class="maincolor"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
	</tr>
	<tr><td class="maincolor" colspan="3"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td></tr>
</table>
</mm:node
></mm:list
></mm:cloud>
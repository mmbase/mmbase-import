<%@include file="/taglibs.jsp" %>
<%@page import="java.text.*" %>
<%@include file="../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<% 
NumberFormat nf = NumberFormat.getInstance(Locale.FRENCH);
nf.setMaximumFractionDigits(2);
nf.setMinimumFractionDigits(2);
PaginaHelper ph = new PaginaHelper(cloud);
String shop_itemHref = "";
%>
<mm:list nodes="<%= paginaID %>" path="pagina,contentrel,artikel" constraints="contentrel.pos = '2'"
   ><mm:node element="artikel"
	><table width="100%" cellspacing="0" cellpadding="0" class="discount">
	<tr>
		<td class="titlebar"><img src="media/discountborder.gif" width="1" height="15" border="0" alt=""></td>
		<td class="nav" background="<mm:related path="posrel,images" max="1"
					><mm:node element="images"
						><mm:image template="s(140x180)" 
					/></mm:node
				></mm:related>" style="padding:4px;">
		<img src="media/spacer.gif" width="1" height="15" border="0" alt=""><br>
		<mm:field name="titel_eng"
			><mm:isnotempty
				><mm:compare value="standaard" inverse="true"
					><img style="float:right;margin-top:-11px;margin-right:5px;" src="media/<mm:write />.gif"></mm:compare
			></mm:isnotempty
		></mm:field
		><mm:field name="titel" 
		/><table cellspacing="0" cellpadding="0" width="100%">
			<tr>
				<td width="60%">
				<mm:field name="intro" jspvar="text" vartype="String" write="false">
				   <%= (text!=null ? HtmlCleaner.cleanText(text,"<",">","") : "") %>
				</mm:field>
				<br/>
				<mm:related path="readmore,products,posrel,pagina" fields="products.number,pagina.number"
					orderby="readmore.readmore" directions="UP"
					><mm:field name="pagina.number" jspvar="pages_number" vartype="String" write="false"
					><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"><%
						shop_itemHref =  ph.createPaginaUrl(paginaID,request.getContextPath()) + "?u=" + products_number; 
					%></mm:field
					></mm:field
					><a href="<mm:url page="<%= shop_itemHref %>" />" class="bold"><mm:field name="products.titel" /></a><br>
					<mm:last
						><mm:node element="products"
							><mm:import id="smallprice" 
							/><%@include file="relatedprice.jsp" 
							%><mm:remove referid="smallprice" 
						/></mm:node
					></mm:last>
				</mm:related> 
				</td>
				<td style="width:40%;text-align:right;vertical-align:bottom;""><a style="display:block;width:auto;height:70;" href="<mm:url page="<%= shop_itemHref %>" 
					/>" ><img src="media/spacer.gif" border="0" alt=""></a></td>		
			</tr>
		</table>
		</td>
		<td class="titlebar"><img src="media/discountborder.gif" width="1" height="15" border="0" alt=""></td>
	</tr>
	<tr>
		<td class="titlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
		<td class="footer" width="100%">
		<table cellspacing="0" cellpadding="0" align="right"><tr>
			<td class="nav"><a href="<mm:url page="<%= shop_itemHref %>" />" class="nav"><mm:field name="titel_fra" /></a></td>
			<td style="padding:2px;padding-left:5px;"><a href="<mm:url page="<%= shop_itemHref %>" />"><img src="media/pijl_oranje_op_lichtoranje.gif" border="0" alt=""></a></td>
		</tr></table>
		</td>
		<td class="titlebar"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td>
	</tr>
	<tr><td class="titlebar" colspan="3"><img src="media/spacer.gif" width="1" height="1" border="0" alt=""></td></tr>
</table>
</mm:node
></mm:list
></mm:cloud>
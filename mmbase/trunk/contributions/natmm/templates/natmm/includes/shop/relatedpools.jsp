<%
LinkedList llPagesWithItems = new LinkedList();
%><mm:list nodes="<%= rubriekID %>" path="rubriek,posrel,pagina"
		orderby="posrel.pos" directions="UP" fields="pagina.number"
	><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"
		><mm:list nodes="<%= pagina_number %>" path="pagina,posrel,items" max="1"><%
		  llPagesWithItems.add(pagina_number);
		%></mm:list
	></mm:field
></mm:list
><mm:import id="nointro" 
/><mm:import id="imageonly"
/><%

while(llPagesWithItems.size()>0) {
	String leftPaginaNumber = (String) llPagesWithItems.removeFirst();
	String leftShop_itemNumber = "";
	String leftShop_itemHref = "";
	%><mm:list nodes="<%= leftPaginaNumber %>" path="pagina,posrel,items"
		orderby="posrel.pos" directions="UP" max="1" fields="items.number"
		><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"><%
		   leftShop_itemNumber = items_number;
		%></mm:field
	></mm:list><%
	
	leftShop_itemHref = ph.createPaginaUrl(leftPaginaNumber,request.getContextPath());
	
	String rightPaginaNumber = "";
	String rightShop_itemNumber = "";
	String rightShop_itemHref = "";
	boolean rightShop_itemExists = false;
	if(llPagesWithItems.size()>0) { 
		rightPaginaNumber = (String) llPagesWithItems.removeFirst();
		%><mm:list nodes="<%= rightPaginaNumber %>" path="pagina,posrel,items"
			orderby="posrel.pos" directions="UP" max="1" fields="items.number"
			><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"><%
			   rightShop_itemNumber = items_number;
			%></mm:field
		></mm:list><%
		 
		rightShop_itemHref = ph.createPaginaUrl(rightPaginaNumber,request.getContextPath());
		rightShop_itemExists = true;
	} 
	
	%><img src="media/trans.gif" width="1" height="10" border="0" alt=""><br>
	<table cellspacing="0" cellpadding="0" width="100%">
			<%@include file="../items/titlerow.jsp" 
			%><%@include file="../items/imagerow.jsp" %>
			<tr>
				<td style="width:100%;" colspan="3">
				<table cellspacing="0" cellpadding="0" style="width:100%"><tr>
				<mm:node number="<%= leftPaginaNumber %>" 
					><td class="maincolor"
						style="width:50%;text-align:right;vertical-align:bottom;padding-left:4px;padding-right:2px;padding-bottom:2px;font-size:12px;">
						<strong><bean:message bundle="LEOCMS" key="shop.relatedpools.more" /></strong>
            <a href="<mm:url page="<%= leftShop_itemHref %>" />" class="klikpad"><mm:field name="titel" /></a></td>
					<td class="maincolor" width="0%" style="vertical-align:bottom;padding:2px;">
						<a href="<mm:url page="<%= leftShop_itemHref %>" />"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
				</mm:node>
				<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
				<% if(rightShop_itemExists) { 
						%><mm:node number="<%= rightPaginaNumber %>" 
						><td class="maincolor" 
							style="width:50%;text-align:right;vertical-align:bottom;padding-left:4px;padding-right:2px;padding-bottom:2px;font-size:12px;">
							<strong><bean:message bundle="LEOCMS" key="shop.relatedpools.more" /></strong>
              <a href="<mm:url page="<%= rightShop_itemHref %>" />" class="klikpad"><mm:field name="titel" /></a></td>
						<td class="maincolor" width="0%" style="vertical-align:bottom;padding:2px;">
							<a href="<mm:url page="<%= rightShop_itemHref %>" />"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
						</mm:node><% 
				} else {
					%><td width="50%">&nbsp;</td><td width="0%">&nbsp;</td><%
				} %>
				</tr></table>
				</td>
			</tr>
	</table><% 
} 
%><mm:remove referid="nointro" 
/><mm:remove referid="imageonly" />

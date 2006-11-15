<% LinkedList llPagesWithProducts = new LinkedList();
%><mm:list nodes="<%= subsiteID %>" path="rubriek,posrel,pagina"
		orderby="posrel.pos" directions="UP" fields="pagina.number"
	><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"
		><mm:list nodes="<%= pagina_number %>" path="pagina,posrel,products" max="1"
			><% llPagesWithProducts.add(pagina_number);
		%></mm:list
	></mm:field
></mm:list
><mm:import id="nointro" 
/><mm:import id="imageonly"
/><%

while(llPagesWithProducts.size()>0) {
	String leftPaginaNumber = (String) llPagesWithProducts.removeFirst();
	String leftProductNumber = "";
	String leftProductHref = "";
	%><mm:list nodes="<%= leftPaginaNumber %>" path="pagina,posrel,products"
		orderby="posrel.pos" directions="UP" max="1" fields="products.number"
		><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
			><% leftProductNumber = products_number;
		%></mm:field
	></mm:list
	><% leftProductHref = pageUrl + "&p=" + leftPaginaNumber;
	
	String rightPaginaNumber = "";
	String rightProductNumber = "";
	String rightProductHref = "";
	boolean rightProductExists = false;
	if(llPagesWithProducts.size()>0) { 
		rightPaginaNumber = (String) llPagesWithProducts.removeFirst();
		%><mm:list nodes="<%= rightPaginaNumber %>" path="pagina,posrel,products"
			orderby="posrel.pos" directions="UP" max="1" fields="products.number"
			><mm:field name="products.number" jspvar="products_number" vartype="String" write="false"
				><% rightProductNumber = products_number;
			%></mm:field
		></mm:list
		><% rightProductHref = pageUrl + "&p=" + rightPaginaNumber;

		rightProductExists = true;
	} 
	
	%><img src="media/spacer.gif" width="1" height="10" border="0" alt=""><br>
	<table cellspacing="0" cellpadding="0" width="100%">
			<%@include file="../items/titlerow.jsp" 
			%><%@include file="../items/imagerow.jsp" %>
			<tr>
				<td style="width:100%;" colspan="3">
				<table cellspacing="0" cellpadding="0" style="width:100%"><tr>
				<mm:node number="<%= leftPaginaNumber %>" 
					><td class="titlebar"
						style="width:50%;text-align:right;vertical-align:bottom;padding-left:4px;padding-right:2px;padding-bottom:2px;font-size:12px;">
						<strong>meer</strong> <a href="<mm:url page="<%= leftProductHref %>" />" class="readmore"><mm:field name="name" /></a></td>
					<td class="titlebar" width="0%" style="vertical-align:bottom;padding:2px;">
						<a href="<mm:url page="<%= leftProductHref %>" />"><img src="media/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
				</mm:node>
				<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
				<% if(rightProductExists) { 
						%><mm:node number="<%= rightPaginaNumber %>" 
						><td class="titlebar" 
							style="width:50%;text-align:right;vertical-align:bottom;padding-left:4px;padding-right:2px;padding-bottom:2px;font-size:12px;">
							<strong>meer</strong> <a href="<mm:url page="<%= rightProductHref %>" />" class="readmore"><mm:field name="name" /></a></td>
						<td class="titlebar" width="0%" style="vertical-align:bottom;padding:2px;">
							<a href="<mm:url page="<%= rightProductHref %>" />"><img src="media/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
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

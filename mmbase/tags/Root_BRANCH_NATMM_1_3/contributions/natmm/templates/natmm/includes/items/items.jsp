<%@include file="/taglibs.jsp" %>
<%@include file="../request_parameters.jsp" %>
<%@include file="../shoppingcart/vars.jsp" %>
<mm:cloud jspvar="cloud">
<%
LinkedList shop_items = new LinkedList();
%><mm:list nodes="<%= paginaID %>" path="pagina,posrel,items" fields="items.number,items.titel"
	orderby="posrel.pos,items.titel" directions="DOWN,UP"
   ><mm:field name="items.number" jspvar="shop_items_number" vartype="String" write="false"><% 
    shop_items.add(shop_items_number);
   %></mm:field
></mm:list><%

while(shop_items.size()>0) {

  PaginaHelper ph = new PaginaHelper(cloud);
  
  String paginaUrl =  ph.createPaginaUrl(paginaID,request.getContextPath());
	String leftShop_itemNumber = (String) shop_items.removeFirst();
	String leftShop_itemHref =  paginaUrl + "?u=" + leftShop_itemNumber;
	
	String rightShop_itemNumber = "";
	String rightShop_itemHref = "";
	boolean rightShop_itemExists = false;
	if(shop_items.size()>0) { 
		rightShop_itemNumber = (String) shop_items.removeFirst();
		rightShop_itemHref = paginaUrl + "?u=" + rightShop_itemNumber;;
		rightShop_itemExists = true;
	} 
	%><table width="100%" cellspacing="0" cellpadding="0">
	<%@include file="titlerow.jsp" %>
	<tr>
		<mm:node number="<%= leftShop_itemNumber %>"
			><td style="padding-right:3px;vertical-align:middle;"><%@include file="price.jsp"%></td></mm:node>
		<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
		<% 
    if(rightShop_itemExists) { 
			%><mm:node number="<%= rightShop_itemNumber %>"
			><td style="padding-right:3px;vertical-align:middle;"><%@include file="price.jsp" %></td></mm:node><% 
		} else {
			%><td>&nbsp;</td><%
		} %>
	</tr>
	<%@include file="imagerow.jsp" %>
	<tr>
		<%
		// don't use urlConversion here because shop_items are not related to the page "bestel"
		shop_itemHref = "shoppingcart.jsp?p=bestel&u=" + leftShop_itemNumber;
		%><mm:node number="<%= leftShop_itemNumber %>"><%@include file="shoppingcart.jsp"%></mm:node>
		<td><img src="media/trans.gif" height="40" width="8" border="0" alt=""></td>
		<% 
		if(rightShop_itemExists) {
			shop_itemHref =  "shoppingcart.jsp?p=bestel&u=" + rightShop_itemNumber;
			%><mm:node number="<%= rightShop_itemNumber %>"><%@include file="shoppingcart.jsp" %></mm:node><% 
		} else {
			%><td>&nbsp;</td><%
		} %>
	</tr>
   </table>
   <img src="media/trans.gif" width="1" height="16" border="0" alt=""><br>
   <% 
} %>
</mm:cloud>
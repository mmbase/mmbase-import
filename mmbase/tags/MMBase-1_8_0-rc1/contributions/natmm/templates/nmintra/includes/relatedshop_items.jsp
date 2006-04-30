<% LinkedList shop_items = new LinkedList();
%><mm:list nodes="<%= pageId %>" path="pagina,posrel,items"
	orderby="posrel.pos,items.titel" directions="UP,UP" fields="items.number"
><mm:field name="items.number" jspvar="shop_items_number" vartype="String" write="false"
	><% shop_items.add(shop_items_number);
%></mm:field
></mm:list><%

while(shop_items.size()>0) {
	String leftShop_itemNumber = (String) shop_items.removeFirst(); 
	String leftShop_itemHref = pageUrl + "?p=" + pageId + "&u=" + leftShop_itemNumber; 
	
	String rightShop_itemNumber = "";
	String rightShop_itemHref = "";
	boolean rightShop_itemExists = false;
	if(shop_items.size()>0) { 
		rightShop_itemNumber = (String) shop_items.removeFirst();
		rightShop_itemHref =  pageUrl + "&p=" + pageId + "&u=" + rightShop_itemNumber; 
		rightShop_itemExists = true;
	} 
	%><table width="100%" cellspacing="0" cellpadding="0">
	<%@include file="../includes/shop_itemtitlerow.jsp" %>
	<tr>
		<mm:node number="<%= leftShop_itemNumber %>"
			><td class="middle" style="padding-right:3px;"><%@include file="../includes/relatedprice.jsp"%></td></mm:node>
		<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
		<% if(rightShop_itemExists) { 
			%><mm:node number="<%= rightShop_itemNumber %>"
			><td class="middle" style="padding-right:3px;"><%@include file="../includes/relatedprice.jsp" %></td></mm:node><% 
		} else {
			%><td>&nbsp;</td><%
		} %>
	</tr>
	<%@include file="../includes/shop_itemimagerow.jsp" %>
	<tr>				
		<% shop_itemHref = pageUrl + "&p=bestel&u=" + leftShop_itemNumber;
		%><mm:node number="<%= leftShop_itemNumber %>"><%@include file="../includes/relatedshoppingcart.jsp"%></mm:node>
		<td><img src="media/spacer.gif" height="40" width="8" border="0" alt=""></td>
		<% if(rightShop_itemExists) {
			shop_itemHref = pageUrl + "&p=bestel&u=" + rightShop_itemNumber;
			%><mm:node number="<%= rightShop_itemNumber %>"><%@include file="../includes/relatedshoppingcart.jsp" %></mm:node><% 
		} else {
			%><td>&nbsp;</td><%
		} %>
	</tr>
   </table>
   <img src="media/spacer.gif" width="1" height="16" border="0" alt=""><br>
   <% 
} %>

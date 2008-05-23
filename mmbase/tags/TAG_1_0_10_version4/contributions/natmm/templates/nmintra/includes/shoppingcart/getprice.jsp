<% if(memberId.equals("")) {
	%><mm:field name="price1" jspvar="shop_items_price1" vartype="String" write="false"
		><% price = Integer.parseInt(shop_items_price1);
	%></mm:field><%
} else {
	%><mm:field name="price3" jspvar="shop_items_price1" vartype="String" write="false"
		><% price = Integer.parseInt(shop_items_price1);
	%></mm:field><%
} %>

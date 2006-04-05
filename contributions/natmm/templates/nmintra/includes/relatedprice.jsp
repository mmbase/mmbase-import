<mm:field name="price1" jspvar="shop_items_price1" vartype="String" write="false"><%
if(bShowPrices&&!shop_items_price1.equals("-1")&&!shop_items_price1.equals("0")) { 
%><table cellspacing="0" cellpadding="0" <mm:notpresent referid="smallprice">width="100%"</mm:notpresent>>
	<tr><td class="nav" style="font-size:12px;">PRIJS</td><td class="right" style="padding-left:2px;">
		<mm:field name="price2" jspvar="shop_items_price2" vartype="String" write="false"><%
			if(!shop_items_price2.equals("-1")) { 
				%><span style="text-decoration: line-through">&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(shop_items_price2))/100) %></span><%
			} 
		%></mm:field>
		<%
			if(!shop_items_price1.equals("-1")) { 
				%><strong>&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(shop_items_price1))/100) %></strong><%
			} else {
				%>nog onbekend<%
			}
		%>
	</td></tr><%
	if(bMemberDiscount) { 
	%><tr><td class="nav" style="font-size:12px;">LEDENPRIJS</td><td class="right" style="padding-left:2px;">
		<mm:field name="price4" jspvar="shop_items_price4" vartype="String" write="false"><%
			if(!shop_items_price4.equals("-1")) { 
				%><span style="text-decoration: line-through">&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(shop_items_price4))/100) %></span><%
			} 
		%></mm:field
		> <mm:field name="price3" jspvar="shop_items_price3" vartype="String" write="false"><%
			if(!shop_items_price3.equals("-1")) { 
				%><strong>&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(shop_items_price3))/100) %></strong><%
			} else {
				%>nog onbekend<%
			}
		%></mm:field
	></td></tr><%
	} %>
</table><%
} %>
</mm:field>

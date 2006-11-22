<table cellspacing="0" cellpadding="0" <mm:notpresent referid="smallprice">width="100%"</mm:notpresent>>
	<tr><td class="nav" style="font-size:12px;">PRIJS</td><td class="right" style="padding-left:2px;">
		<mm:field name="price2" jspvar="products_price2" vartype="String" write="false"><%
			if(!products_price2.equals("-1")) { 
				%><span style="text-decoration: line-through">&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(products_price2))/100) %></span><%
			} 
		%></mm:field
		> <mm:field name="price1" jspvar="products_price1" vartype="String" write="false"><%
			if(!products_price1.equals("-1")) { 
				%><strong>&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(products_price1))/100) %></strong><%
			} else {
				%>nog onbekend<%
			}
		%></mm:field
	></td></tr>
	<tr><td class="nav" style="font-size:12px;">LEDENPRIJS</td><td class="right" style="padding-left:2px;">
		<mm:field name="price4" jspvar="products_price4" vartype="String" write="false"><%
			if(!products_price4.equals("-1")) { 
				%><span style="text-decoration: line-through">&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(products_price4))/100) %></span><%
			} 
		%></mm:field
		> <mm:field name="price3" jspvar="products_price3" vartype="String" write="false"><%
			if(!products_price3.equals("-1")) { 
				%><strong>&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(products_price3))/100) %></strong><%
			} else {
				%>nog onbekend<%
			}
		%></mm:field
	></td></tr>
</table>

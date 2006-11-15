<% if(memberId.equals("")) {
	%><mm:field name="price1" jspvar="products_price1" vartype="String" write="false"
		><% price = Integer.parseInt(products_price1);
	%></mm:field><%
} else {
	%><mm:field name="price3" jspvar="products_price1" vartype="String" write="false"
		><% price = Integer.parseInt(products_price1);
	%></mm:field><%
} %>

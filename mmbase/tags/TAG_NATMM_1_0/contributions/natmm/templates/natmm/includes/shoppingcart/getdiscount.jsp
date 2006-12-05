<%--
Discounts are given in two includes:
- getdiscount.jsp for the product specific discounts
- getgeneraldiscount.jsp for the general discounts

1: (combi) in de actieperiode een korting van het kortings bedrag
2: (combi) in de actieperiode dit artikel gratis
3: in de actieperiode een korting van het kortings bedrag
4: in de actieperiode geen verzendkosten

*************** 1,2: combi products ********************** 
--%><mm:field name="number" jspvar="thisproduct" vartype="String" write="false"
><mm:related path="discountrel,pools"
><mm:field name="discountrel.startdate" jspvar="startdate" vartype="String" write="false"
><mm:field name="discountrel.enddate" jspvar="enddate" vartype="String" write="false"><%
	if(Long.parseLong(startdate)<=Now && Now<=Long.parseLong(enddate)) { 
		%><mm:node element="pools"
		><mm:related path="posrel,products"
			><mm:field name="products.number" jspvar="combiproduct" vartype="String" write="false"
				><% if(products.get(combiproduct)!=null&&!combiproduct.equals(thisproduct)) { 
					%><mm:remove referid="combiproduct"
					/><mm:import id= "combiproduct" /><%
				} 
		%></mm:field
		></mm:related
		></mm:node
		><mm:present referid="combiproduct"
			><mm:field name="discountrel.type" jspvar="type" vartype="String" write="false"
			><mm:field name="discountrel.amount" jspvar="discounts_amount" vartype="String" write="false"><%
			int amount = Integer.parseInt(discounts_amount); 
			if(type.equals("1")&&(amount<=price)) { discount = amount; }
			if(type.equals("2")) { discount = price; }
			%></mm:field
			></mm:field
		></mm:present
		><mm:remove referid="combiproduct" /><%
	} 
%></mm:field
></mm:field
></mm:related
></mm:field
><%--

*************** 3,4: discount on product ********************** 
--%><mm:related path="posrel,discounts"
><mm:field name="discounts.startdate" jspvar="startdate" vartype="String" write="false"
><mm:field name="discounts.enddate" jspvar="enddate" vartype="String" write="false"><%
if(Long.parseLong(startdate)<=Now && Now<=Long.parseLong(enddate)) { 
	%><mm:field name="discounts.type" jspvar="type" vartype="String" write="false"
	><mm:field name="discounts.amount" jspvar="discounts_amount" vartype="String" write="false"><%
	int amount = Integer.parseInt(discounts_amount);
	if(type.equals("3")) { discount = amount; }
	if(type.equals("4")) { shippingCosts = 0; }
	%></mm:field
	></mm:field><%
} %></mm:field
></mm:field
></mm:related>


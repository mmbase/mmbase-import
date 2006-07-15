<%--
Discounts are given in two includes:
- getdiscount.jsp for the shop_item specific discounts
- getgeneraldiscount.jsp for the general discounts

1: (combi) in de actieperiode een korting van het kortings bedrag
2: (combi) in de actieperiode dit artikel gratis
3: in de actieperiode een korting van het kortings bedrag
4: in de actieperiode geen verzendkosten

*************** 1,2: combi shop_items ********************** 
--%><mm:field name="number" jspvar="thisshop_item" vartype="String" write="false"
><mm:related path="discountrel,pools"
><mm:field name="discountrel.startdate" jspvar="startdate" vartype="String" write="false"
><mm:field name="discountrel.enddate" jspvar="enddate" vartype="String" write="false"><%
	if(Long.parseLong(startdate)<=nowSec && nowSec<=Long.parseLong(enddate)) { 
		%><mm:node element="pools"
		><mm:related path="posrel,items"
			><mm:field name="items.number" jspvar="combishop_item" vartype="String" write="false"
				><% if(shop_items.get(combishop_item)!=null&&!combishop_item.equals(thisshop_item)) { 
					%><mm:remove referid="combishop_item"
					/><mm:import id= "combishop_item" /><%
				} 
		%></mm:field
		></mm:related
		></mm:node
		><mm:present referid="combishop_item"
			><mm:field name="discountrel.type" jspvar="type" vartype="String" write="false"
			><mm:field name="discountrel.amount" jspvar="discounts_amount" vartype="String" write="false"><%
			int amount = Integer.parseInt(discounts_amount); 
			if(type.equals("1")&&(amount<=price)) { discount = amount; }
			if(type.equals("2")) { discount = price; }
			%></mm:field
			></mm:field
		></mm:present
		><mm:remove referid="combishop_item" /><%
	} 
%></mm:field
></mm:field
></mm:related
></mm:field
><%--

*************** 3,4: discount on shop_item ********************** 
--%><mm:related path="posrel,discounts"
><mm:field name="discounts.startdate" jspvar="startdate" vartype="String" write="false"
><mm:field name="discounts.enddate" jspvar="enddate" vartype="String" write="false"><%
if(Long.parseLong(startdate)<=nowSec && nowSec<=Long.parseLong(enddate)) { 
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


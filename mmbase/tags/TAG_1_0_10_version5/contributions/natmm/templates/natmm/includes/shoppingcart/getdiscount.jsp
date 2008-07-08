<%
/*
Discounts are given in two includes:
- getdiscount.jsp for the product specific discounts
- getgeneraldiscount.jsp for the general discounts

1: (combi) in de actieperiode een korting van het kortings bedrag
2: (combi) in de actieperiode dit artikel gratis
3: in de actieperiode een korting van het kortings bedrag
4: in de actieperiode geen verzendkosten

*************** 1,2: combi products **********************
*/
%><mm:field name="number" jspvar="thisproduct" vartype="String" write="false"
><mm:related path="discountrel,pools"
><mm:field name="discountrel.startdate" jspvar="startdate" vartype="Long" write="false"
><mm:field name="discountrel.enddate" jspvar="enddate" vartype="Long" write="false"><%
	if(startdate.longValue()<=nowSec && nowSec<=enddate.longValue()) { 
		%><mm:node element="pools"
		><mm:related path="posrel,items"
			><mm:field name="items.number" jspvar="combi_item" vartype="String" write="false"><% 
        if(products.get(combi_item)!=null&&!combi_item.equals(thisproduct)) { 
					%><mm:remove referid="combi_item"
					/><mm:import id= "combi_item" /><%
				} 
		%></mm:field
		></mm:related
		></mm:node
		><mm:present referid="combi_item"
			><mm:field name="discountrel.type" jspvar="type" vartype="String" write="false"
			><mm:field name="discountrel.amount" jspvar="discounts_amount" vartype="String" write="false"><%
			int amount = Integer.parseInt(discounts_amount); 
			if(type.equals("1")&&(amount<=price)) { discount = amount; }
			if(type.equals("2")) { discount = price; }
			%></mm:field
			></mm:field
		></mm:present
		><mm:remove referid="combi_item" /><%
	} 
%></mm:field
></mm:field
></mm:related
></mm:field><%

// ************ 3,4: discount on product ********************** 
%><mm:related path="posrel,discounts"
><mm:field name="discounts.startdate" jspvar="startdate" vartype="Long" write="false"
><mm:field name="discounts.enddate" jspvar="enddate" vartype="Long" write="false"><%
if(startdate.longValue()<=nowSec && nowSec<=enddate.longValue()) { 
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


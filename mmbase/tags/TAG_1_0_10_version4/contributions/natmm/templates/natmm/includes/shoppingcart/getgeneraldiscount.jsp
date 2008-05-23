<%
/*
Discounts are given in two includes:
- getdiscount.jsp for the product specific discounts
- getgeneraldiscount.jsp for the general discounts

5: (alg) boven de grenswaarde een korting van het kortings bedrag
6: (alg) boven de grenswaarde geen verzendkosten
7: (alg) via internet een korting van het kortings percentage
8: (alg) via internet geen verzendkosten

*************** 5,6,7,8: general discounts **********************
*/
%><%
int generaldiscount = 0;
%><mm:list nodes="<%= paginaID %>" path="pagina,posrel,discounts"
><mm:field name="discounts.startdate" jspvar="startdate" vartype="Long" write="false"
><mm:field name="discounts.enddate" jspvar="enddate" vartype="Long" write="false"><%
if(startdate.longValue()<=nowSec && nowSec<=enddate.longValue()) { 
	%><mm:field name="discounts.type" jspvar="type" vartype="String" write="false"
	><mm:field name="discounts.amount" jspvar="discounts_amount" vartype="String" write="false"
	><mm:field name="discounts.threshold" jspvar="discounts_threshold" vartype="String" write="false"><%
		int amount = Integer.parseInt(discounts_amount);
		int threshold = Integer.parseInt(discounts_threshold);
		if(type.equals("5")&&(totalSum>threshold)) { generaldiscount = amount; }
		if(type.equals("6")&&(totalSum>threshold)) { shippingCosts = 0; }
		if(type.equals("7")) { generaldiscount = (amount*totalSum)/100; }
		if(type.equals("8")) { shippingCosts = 0; }
	%></mm:field
	></mm:field
	></mm:field><%
} %></mm:field
></mm:field
></mm:list>

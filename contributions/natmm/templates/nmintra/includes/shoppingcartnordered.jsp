<%
// *** pools have to be repeated when they need unique answers per ordered product ***
int numberOrdered = 0;
%><mm:list nodes="<%= allShop_items %>" path="items,posrel,pools" orderby="items.title" directions="UP" 
  	fields="items.number" distinct="yes" constraints="<%= "pools.number = '"  + thisPool + "' AND pools.view = 'shop_repeat'" %>"
  	><mm:field name="items.number" jspvar="thisShop_item" vartype="String" write="false"><%
     	String numberOfItems = (String) shop_items.get(thisShop_item);
     	if(numberOfItems!=null) {
     	   numberOrdered += Integer.parseInt(numberOfItems);
      }
   %></mm:field
></mm:list><%
if(numberOrdered == 0) { numberOrdered = 1; } 
%>
<%	 
if(bShowPrices) {
   offsetId = (String) session.getAttribute("totalcosts"); 
	if(offsetId !=null) { 
		%><img src="media/spacer.gif" width="1" height="11" border="0" alt=""><br>
		<table width="100%" cellspacing="0" cellpadding="0">
		<tr><td class="titlebar" colspan="2"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
		<tr><td class="carteven"  style="text-align:left;"><span style="font-size:12px;font-weight:bold;">TOTALE KOSTEN</span></td>
			<td class="carteven" style="font-size:12px;font-weight:bold;padding-right:5px;text-align:right;"><%
			if(!offsetId.equals("-1")) { 
				%>&euro;&nbsp;<%= nf.format(((double) Integer.parseInt(offsetId))/100) %><%
			} else {
				%>nog onbekend<% 
			} %></td></tr>
		<tr><td class="titlebar" colspan="2"><img src="media/spacer.gif" height="1" width="1" border="0" alt=""></td></tr>
		</table><%
	}
} 
%>
<form name="emailform" method="post" target=""> 
<table border="0" align="left" width="400px;">
   <tr><td class="cartheader" style="padding-left:10px;text-align:left;">
	    Vul de onderstaande vragen in om de geselecteerde producten te bestellen.<br/><br/>
  </td></tr>
  <% 
   	shop_itemsIterator = (TreeMap) shop_items.clone();
   	String allShop_items = "";
   	String tmpSepar = "";
   
   	while(shop_itemsIterator.size()>0) { 
   		String thisShop_item = (String) shop_itemsIterator.firstKey();
   		allShop_items += tmpSepar + thisShop_item;
   		tmpSepar = ",";
   		shop_itemsIterator.remove(thisShop_item);
   	}
   	String thisPool = null;
   // *** for all pools, related to the shop items ordered ***
   %><mm:list nodes="<%= allShop_items %>" path="items,posrel,pools"
  	   orderby="pools.language" directions="UP" searchdir="destination" 
      fields="pools.number" distinct="true"
   	><mm:node element="pools">
   	   <tr><td style="padding-left:10px;">
   		<mm:field name="number" jspvar="dummy" vartype="String" write="false"
   			><% thisPool = dummy; 
   		%></mm:field
   		><mm:field name="name" jspvar="pools_name" vartype="String" write="false"
   			><% if(pools_name.indexOf("#NZ#")==-1) { %><h4><%= pools_name %></h4><% } 
   		%></mm:field>
           <mm:field name="description" jspvar="pools_description" vartype="String" write="false"
               ><mm:isnotempty><span class="black"><%
               if(pools_description.toLowerCase().indexOf("<p>")==-1) { 
                   %><p><%=pools_description %></p><% 
               } else { 
                   %><%= pools_description %><% 
               } 
               %></span></mm:isnotempty
           ></mm:field><%
           // *** for all shop items, that use this pool ***
           if(shop_items.size()>1) { 
               %><mm:list nodes="<%= allShop_items %>" path="items,posrel,pools" orderby="items.title" directions="UP" 
         	   	fields="items.number" distinct="true" constraints="<%= "pools.number = '"  + thisPool + "'" %>"
         		   ><mm:first>Antwoorden zijn nodig voor:</mm:first
         		   ><mm:node element="items"
         		      ><li><mm:field name="title" />
               	   <mm:field name="number" jspvar="thisShop_item" vartype="String" write="false"><%
                  	   String numberOfItems = (String) shop_items.get(thisShop_item);
                  	   %>(<%= numberOfItems %> items)<%
   		            %></mm:field
   		         ></mm:node
   		      ></mm:list><br/><br/><% 
   	     } 
   	     %><%@include file="../includes/shoppingcartnordered.jsp" %><%
   	     for(int i =0; i< numberOrdered; i++) {
   	         %><%@include file="../includes/shoppingcartquestions.jsp" %><% 
           }
         %></td></tr>
       </mm:node
    ></mm:list>
      <tr>
         <td style="padding-left:10px;">
         <table width="180" cellspacing="0" cellpadding="0">
            <tr>
            <td class="titlebar" style="vertical-align:middle;padding-left:4px;padding-right:2px;" width="100%">
            	<nowrap><a href="javascript:changeIt('<mm:url page="<%= pageUrl + "&p=bestel&t=send" %>" />');"
                        onclick="needToConfirm = false;" class="white">Verstuur</a></td>
            <td class="titlebar" style="padding:2px;" width="100%">
            	<a href="javascript:changeIt('<mm:url page="<%= pageUrl + "&p=bestel&t=send" %>" />');" 
                  onclick="needToConfirm = false;"><img src="media/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
            </tr>
      	</table>
      	</td>
      </tr>
      <tr>
         <td style="padding-left:10px;"><br>Vul minimaal deze velden in i.v.m. een correcte afhandeling (*)<br><br></td>
      </tr>
   </table>
</form>

<br/><br/>
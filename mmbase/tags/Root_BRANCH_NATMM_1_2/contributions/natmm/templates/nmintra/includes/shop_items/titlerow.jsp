<tr>
	<mm:node number="<%= leftShop_itemNumber %>"><td width="50%" class="nav"><mm:field name="subtitle" /></td></mm:node>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<% if(rightShop_itemExists) { 
		%><td width="50%" class="nav"><mm:node number="<%= rightShop_itemNumber %>"><mm:field name="subtitle" /></td></mm:node><% 
	} else {
		%><td width="50%">&nbsp;</td><%
	} %>
</tr>
<tr>
	<mm:node number="<%= leftShop_itemNumber %>"
		><td style="padding-right:3px;"><a href="<mm:url page="<%= leftShop_itemHref %>" />" class="bold"><mm:field name="titel" /></a><br>
		<mm:notpresent referid="nointro"><mm:field name="intro" jspvar="shop_item_intro" vartype="String" write="false"><%
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"<p>","");
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"<P>","");
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"</p>","");
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"</P>","");
			%><%= shop_item_intro %></mm:field
		></mm:notpresent></td></mm:node>
	<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<% if(rightShop_itemExists) { 
		%><mm:node number="<%= rightShop_itemNumber %>" 
			><td style="padding-right:3px;"><a href="<mm:url page="<%= rightShop_itemHref %>" />" class="bold"><mm:field name="titel" /></a><br>
			<mm:notpresent referid="nointro"><mm:field name="intro" jspvar="shop_item_intro" vartype="String" write="false"><%
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"<p>","");
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"<P>","");
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"</p>","");
			shop_item_intro = HtmlCleaner.replace(shop_item_intro,"</P>","");
			%><%= shop_item_intro %></mm:field
		></mm:notpresent></td></mm:node><% 
	} else {
		%><td>&nbsp;</td><%
	} %>
</tr>
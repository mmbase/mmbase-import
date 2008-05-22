<tr>
	<mm:node number="<%= leftShop_itemNumber %>"><td style="width:50%vertical-align:top;" class="nav"><mm:field name="subtitle" /></td></mm:node>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<% if(rightShop_itemExists) { 
		%><td  style="width:50%;vertical-align:top;" class="nav"><mm:node number="<%= rightShop_itemNumber %>"><mm:field name="subtitle" /></td></mm:node><% 
	} else {
		%><td width="50%">&nbsp;</td><%
	} %>
</tr>
<tr>
	<mm:node number="<%= leftShop_itemNumber %>"
		><td style="padding-right:3px;vertical-align:top;"><a href="<mm:url page="<%= leftShop_itemHref %>" />" class="bold"><mm:field name="titel" /></a><br>
		<mm:notpresent referid="nointro"><mm:field name="intro" /></mm:notpresent></td></mm:node>
	<td width="8"><img src="media/trans.gif" height="1" width="8" border="0" alt=""></td>
	<% if(rightShop_itemExists) { 
		%><mm:node number="<%= rightShop_itemNumber %>" 
			><td style="padding-right:3px;vertical-align:top;"><a href="<mm:url page="<%= rightShop_itemHref %>" />" class="bold"><mm:field name="titel" /></a><br>
			<mm:notpresent referid="nointro"><mm:field name="intro" /></mm:notpresent></td></mm:node><% 
	} else {
		%><td>&nbsp;</td><%
	} %>
</tr>
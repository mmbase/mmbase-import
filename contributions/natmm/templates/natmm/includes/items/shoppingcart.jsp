<td>
   <mm:field name="type" jspvar="shopitem_type" vartype="String" write="false">
   <% if(!shopitem_type.equals("uitverkocht")&&!shopitem_type.equals("niet_te_koop")) { %>
   	<table width="100%" cellspacing="0" cellpadding="0">
   		<tr>
   			<% extendedHref = shop_itemHref + "&t=order";
   			%><td class="maincolor" style="padding-left:5px;" width="0%"><a href="<mm:url page="<%= extendedHref %>" />"><img src="media/shop/w_wagentje.gif" border="0" alt=""></a></td>
   			<td class="maincolor" width="100%" style="font-size:12px;padding-left:1px;"><a href="<mm:url page="<%= extendedHref %>" />" class="klikpad"><b>In winkelwagentje</b></a></td>
   			<td class="maincolor" width="0%" style="vertical-align:bottom;padding:2px;"><a href="<mm:url page="<%= extendedHref %>" />"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
   		</tr>
   		<tr>
   			<td colspan="3"><img src="media/trans.gif" width="1" height="1" border="0" alt=""></td>
   		</tr>
   		<tr>
   			<% extendedHref = shop_itemHref + "&t=fast";
   			%><td class="maincolor" width="0%"><table cellspacing="1" cellpadding="0"><tr><td style="background-color:#FFFFFF;"><a href="<mm:url page="<%= extendedHref %>" />"><img src="media/shop/w_wagentje_op_wit.gif" border="0" alt=""></a></td></tr></table></td>
   			<td class="maincolor" width="100%" style="font-size:12px;padding-left:1px;"><a href="<mm:url page="<%= extendedHref %>" />" class="klikpad"><b>Direct bestellen</b></a></td>
   			<td class="maincolor" width="0%" style="vertical-align:bottom;padding:2px;"><a href="<mm:url page="<%= extendedHref %>" />"><img src="media/shop/pijl_wit_op_oranje.gif" border="0" alt=""></a></td>
   		</tr>
   	</table>
	<% } else { %>
   	<table width="100%" cellspacing="0" cellpadding="0">
   		<tr>
   		   <td class="maincolor" style="padding-left:5px;padding-right:5px;color:#000000;font-weight:bold;">
   		   <% if(shopitem_type.equals("uitverkocht")) { 
   		         %>Dit product is (tijdelijk) uitverkocht<%
   		      } else { 
   		         %>Dit product bevat alleen informatie<%
   		      }
   		   %>
   		   </td>
   		</tr>
      </table>
	<% } %>
	</mm:field>
</td>

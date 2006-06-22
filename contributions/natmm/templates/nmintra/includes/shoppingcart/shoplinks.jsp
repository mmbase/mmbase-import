<mm:remove referid="isfirst"
/><mm:import id="isfirst"
/><img src="media/spacer.gif" height="1" width="180" border="0" alt=""><br>
<table width="100%" cellspacing="0" cellpadding="0">
<tr><td style="padding:4px;padding-top:14px;"><%
	if(shop_items.size()>0) {
	  	shop_itemHref = ph.createPaginaUrl(paginaID,request.getContextPath()) + "?t=proceed";
		%><a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');"	onclick="needToConfirm = false;" class="subtitle">Bestelling versturen</a> 
		<a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" onclick="needToConfirm = false;"><img src="media/forward.gif" border="0" alt=""></a><br/>
		<%
	} 
	if(session.getAttribute("pagerefminone")!=null) {
	   shop_itemHref = ph.createPaginaUrl((String) session.getAttribute("pagerefminone"),request.getContextPath()) + "?t=continue";
		%>
		<a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" onclick="needToConfirm = false;" class="subtitle">Verder gaan met winkelen</a> 
		<a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" onclick="needToConfirm = false;"><img src="media/back.gif" border="0" alt=""></a><br/>
		<%
	} %>
</td></tr>
</table>

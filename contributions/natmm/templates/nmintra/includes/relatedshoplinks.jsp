<mm:remove referid="isfirst"
/><mm:import id="isfirst"
/><img src="media/spacer.gif" height="1" width="180" border="0" alt=""><br>
<table width="100%" cellspacing="0" cellpadding="0">
<tr><td style="padding:4px;padding-top:14px;"><%
	if(shop_items.size()>0) {
		shop_itemHref = pageUrl + "&p=bestel&t=proceed"; 
			%><a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');"
               onclick="needToConfirm = false;" class="subtitle">Bestelling versturen</a> 
			<a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" onclick="needToConfirm = false;"><img src="media/forward.gif" border="0" alt=""></a><br>
	<%
	} 
	shop_itemHref = pageUrl + "&r=shop&t=continue";
	if(session.getAttribute("pagerefminone")!=null) {
	   shop_itemHref += "&p=" + (String) session.getAttribute("pagerefminone");
	}
	%><a href="javascript:changeIt('<mm:url 
		page="<%= shop_itemHref %>" />');" onclick="needToConfirm = false;" class="subtitle">Verder gaan met winkelen</a> 
	<a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" onclick="needToConfirm = false;"><img src="media/back.gif" border="0" alt=""></a><br>
</td></tr>
</table>

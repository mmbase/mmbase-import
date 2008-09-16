<img src="media/trans.gif" height="1" width="180" border="0" alt=""><br/>
<table width="100%" cellspacing="0" cellpadding="0">
<tr><td style="padding:4px;padding-top:14px;"><%
	if(products.size()>0) {
     shop_itemHref = ph.createPaginaUrl(paginaID,request.getContextPath()) + "?t=proceed";
		%><a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" class="maincolor_link">
        <bean:message bundle="LEOCMS" key="shop.relatedshoplinks.submit_order" />
      </a>
      <a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');">
        <img src="media/shop/forward.gif" border="0" alt="">
      </a><br/><%
	}
  if(session.getAttribute("pagerefminone")!=null) {
    shop_itemHref = ph.createPaginaUrl((String) session.getAttribute("pagerefminone"),request.getContextPath()) + "?t=continue";
    %>
    <a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');" class="maincolor_link">
      <bean:message bundle="LEOCMS" key="shop.relatedshoplinks.continue_shopping" />
    </a> 
    <a href="javascript:changeIt('<mm:url page="<%= shop_itemHref %>" />');"> <img src="media/shop/back.gif" border="0" alt=""></a><br/>
    <%
  } %>
</td></tr>
</table>
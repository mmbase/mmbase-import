<table width="100%" cellspacing="0" cellpadding="0">
<tr><td style="padding:4px;padding-top:14px;">
	<a href="javascript:history.go(-1);" class="subtitle">Terug</a>
  <%--
  &nbsp;<a href="javascript:history.go(-1);"><img src="media/back.gif" border="0" alt=""></a><br>
  <%
  if(!shop_itemId.equals("-1")) {
		// *********** to shop_items page and mail a friend *******************
		
		%><a href="<mm:url page="<%= ph.createPaginaUrl(paginaID,request.getContextPath()) %>" />" class="subtitle">Meer <mm:node number="<%= paginaID %>"><mm:field name="title" /></mm:node></a><br><%
		String url = HttpUtils.getRequestURL(request).substring(0); // find the last slash
		int slash = url.lastIndexOf("/");
		url = url.substring(0,slash+1);
		%><mm:node number="<%= shop_itemId %>"
			><mm:field name="title" jspvar="shop_item_title" vartype="String" write="false"
				><% shop_itemHref = "mailto:?subject=" + shop_item_title + "&body=Kijk op " + url + "index.jsp?u=" + shop_itemId;
			%></mm:field
		></mm:node
		><a href="<mm:url page="<%= shop_itemHref %>" />" class="subtitle">Mail een collega </a>&nbsp;<a href="<mm:url page="<%= shop_itemHref %>" />"><img src="media/mail.gif" border="0" alt=""></a><br><%
	}
	--%>
	<mm:present referid="isfirst"
></td></tr>
</table></mm:present>

<mm:field name="titel_eng" jspvar="type" vartype="String" write="false"><%
	if(type.equals("link_set")) { 
		%><mm:related path="readmore,pagina" fields="pagina.number"
			><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"
				><mm:notpresent referid="isfirst"
					><table width="100%" cellspacing="0" cellpadding="0">
					<tr><td style="padding:4px;padding-top:14px;"><mm:import id="isfirst"
				/></mm:notpresent><% 
        shop_itemHref = ph.createPaginaUrl(pagina_number,request.getContextPath()); 
				%><a href="<mm:url page="<%= shop_itemHref %>" />" class="maincolor_link"><b><mm:field name="readmore.readmore" /></b></a><br>
			</mm:field
		></mm:related
		><mm:related path="readmore,items,posrel,pagina" fields="items.number,pagina.number"
			><mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false"
			><mm:field name="items.number" jspvar="items_number" vartype="String" write="false"
				><mm:notpresent referid="isfirst"
					><table width="100%" cellspacing="0" cellpadding="0">
					<tr><td style="padding:4px;padding-top:14px;"><mm:import id="isfirst"
				/></mm:notpresent><%
        shop_itemHref = ph.createPaginaUrl(pagina_number,request.getContextPath()) + "?u=" + items_number; 
				%><a href="<mm:url page="<%= shop_itemHref %>" />" class="maincolor_link"><b><mm:field name="readmore.readmore" /></b></a><br>
			</mm:field
			></mm:field
		></mm:related><% 
	}
%></mm:field>
<%@include file="/taglibs.jsp" %>
<%@include file="../../request_parameters.jsp" %>
<mm:cloud jspvar="cloud">
<mm:node number="<%= shop_itemId %>" 
><mm:field name="number" jspvar="products_number" vartype="String" write="false"
	><% productHref = pageUrl + "&u=" + products_number;
	%><mm:list nodes="<%= shop_itemId %>" nodes="product,posrel,pagina">
		  <mm:field name="pagina.number" jspvar="pagina_number" vartype="String" write="false">
		  	  <% productHref += "&p=" + pagina_number; %>
		  </mm:field>
	  </mm:list>	
	  <tr><td rowspan="3" class="bottom" background="<mm:related path="posrel,images" constraints="posrel.pos=1"
						><mm:node element="images"
							><mm:image template="s(100x50)" 
						/></mm:node
					></mm:related>" style="background-position:center bottom;background-repeat:no-repeat;">
		<mm:field name="type"
			><mm:isnotempty
				><mm:compare value="standaard" inverse="true"
				><img style="float:right;" src="media/<mm:write />.gif"></mm:compare
			></mm:isnotempty
		></mm:field
		><a style="display:block;width:100px;height:40px;" href="<mm:url page="<%= productHref %>" />" ><img src="media/spacer.gif" border="0" alt=""></a></td>
		<td colspan="3" style="width:80%;padding:3px;">
			<a href="<mm:url page="<%= productHref %>" />" class="bold"><mm:field name="title" /></a><br>
			<mm:field name="intro" jspvar="articles_intro" vartype="String" write="false"
				><mm:isnotempty><%@include file="../includes/cleanarticlesintro.jsp" %></mm:isnotempty
			></mm:field>
		</td></tr>
	<tr><td style="width:35%;padding-left:3px;padding-right:3px;padding-top:2px;"><%@include file="../includes/relatedprice.jsp" %></td>
		<td style="width:5%;"><img src="media/spacer.gif" border="0" alt="" width="1" height="1"></td><% 
		productHref += "&p=bestel";
		%><%@include file="../includes/relatedshoppingcart.jsp"%></tr>
	<tr><td colspan="3"><img src="media/spacer.gif" border="0" alt="" width="1" height="6"></td></tr>
	<tr><td class="titlebar" colspan="4"><img src="media/spacer.gif" border="0" alt="" width="1" height="1"></td></tr>
</mm:field
></mm:node
></mm:cloud>
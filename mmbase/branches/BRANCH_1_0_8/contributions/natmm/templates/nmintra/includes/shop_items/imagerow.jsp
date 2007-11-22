<tr>
	<% shop_itemHref = leftShop_itemHref;
	%><mm:node number="<%= leftShop_itemNumber %>"><%@include file="image.jsp"%></mm:node>
	<td><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
	<% if(rightShop_itemExists) { 
		shop_itemHref = rightShop_itemHref;
		%><mm:node number="<%= rightShop_itemNumber %>"><%@include file="image.jsp" %></mm:node><% 
	} else {
		%><td>&nbsp;</td><%
	} %>
</tr>
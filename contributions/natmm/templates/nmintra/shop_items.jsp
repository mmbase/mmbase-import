<%@include file="includes/templateheader.jsp" %>
<mm:cloud jspvar="cloud">
<%@include file="includes/header.jsp" %>
<%@include file="includes/calendar.jsp" %>
<td colspan="2"><%@include file="includes/pagetitle.jsp" %></td>
</tr>
<tr>
<td class="transperant" colspan="2">
<div class="<%= infopageClass %>">
<div style="padding-left:10px;padding-right:10px;"><%@include file="includes/relatedteaser.jsp" %></div>
<table cellspacing="10px" cellpadding="0">
<td colspan="3" width="70%"><% 
if(shop_itemId.equals("-1")) {
	%><%@include file="includes/relatedshop_items.jsp" %><% 
} else {
	%><%@include file="includes/relatedshop_item.jsp" %><% 
} %>
</td>
<td width="8"><img src="media/spacer.gif" height="1" width="8" border="0" alt=""></td>
<td width="180"><% 
if(shop_itemId.equals("-1")) {
	%><%@include file="includes/relatedlinkset.jsp" 
	%><%@include file="includes/poolnav.jsp" %><% 
} else {
	boolean hasExtraInfo = false;
	%><mm:list nodes="<%= shop_itemId %>" path="items,posrel,images" 
			constraints="posrel.pos > 1" max="1"
		><% hasExtraInfo = true;
	%></mm:list><%
	if(!hasExtraInfo) { 
		%><mm:list nodes="<%= shop_itemId %>"	path="items,posrel,attachments" max="1"
			><% hasExtraInfo = true; 
		%></mm:list><% 
	} 
	if(!hasExtraInfo) { 
		%><mm:list nodes="<%= shop_itemId %>" path="items,posrel,artikel" max="1"
			><% hasExtraInfo = true; 
		%></mm:list><%
	}
	if(hasExtraInfo) { 
		%><%@include file="includes/shop_itemnav.jsp" %><%
	} 
	%><%@include file="includes/relatedlinkset.jsp" %><% 
} %></td>
</table>
</div>
</td>
<%@include file="includes/footer.jsp" %>
</mm:cloud>

